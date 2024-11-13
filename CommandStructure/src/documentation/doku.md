# Streaming Systems Dokumentation

# Aufgabe 2

## Write - Seite

### Command - Handler und Commands
Die Write-Seite besteht für diese Aufgabe im Wesentlichen aus einem Commandhandler, welcher die Commands entgegennimmt. 
Auf Ihnen führt er Verifikation durch, aktualisiert das Domänenmodell, und erzeugt die 
nötigen Events, welche Richtung Eventqueue weitergegeben werden sollen. <br/>
Die Commands selbst sind Klassen, die gegen ein Interface programmiert wurden. Der Einfachheit halber enthalten 
Sie nur die nötigen Daten, und keine Logik. Sie sind daher auch als Records definiert. Jegliche Behandlung der 
Commands findet im Commandhandler statt. <br/>
Es ist an dieser Stelle diskutabel, ob dies der richtige Ansatz ist. Sollen die Commands Logik enthalten, um z.B. 
eine bessere Kapselung zu gewährleisten, können Sie nicht als Records definiert werden. Allerdings wirkte der 
gewählte Ansatz für diese Aufgabe angemessen. Außerdem sind so die Commands konsistent zu den Events, was an späterer 
Stelle näher erläutert wird.

### Domänenmodell
Das Domänenmodell besteht für diese Aufgabe, wie in der Aufgabenstellung vorgeschlagen, aus einer Hashmap, die den 
Fahrzeugnamen auf ein Fahrzeugobjekt mapped. Sie wurde in einer Containerklasse gewrapped, die einige grundlegende 
Operationen auf der Hashmap anbietet. Diese Klasse soll später umgebaut werden, um das Domänenmodell aus der Eventqueue 
aufbauen zu können. Sie enthält daher auch noch keine Validierung, zumal diese ja bereits im Commandhandler stattfindet.

### Events und EventQueueService
Der Command - Handler erzeugt Events. Diese überreicht er an den EventQueueService, der Sie, in diesem Fall, an eine 
ActiveMQ - Queue übergibt. Wie vorhin erwähnt, sind sowohl die Commands, als auch die Events als Records definiert. 
Das zahlt sich hier als Stärke aus: um an die Queue übergeben werden zu können, müssen die Events serialisiert werden. 
Records bringen diese Funktionalität automatisch mit. Sowohl Objekt serialisierung als auch Serialisierung zu JSON sind 
ohne Mehraufwand möglich. <br/>
Für unsere Zwecke wurde die Serialisierung zu JSON gewählt. Objekt serialisierung scheint in neueren ActiveMQ - Versionen 
als unsicher zu gelten, die übersendung von Nachrichten im Textformat ist bevorzugt. Da JSON-Serialisierung einfach zu 
integrieren ist, bietet sie sich an. Die "Jackson"-Bibliothek bietet alle benötigten Funktionalitäten, und lässt sich 
über annotation von Objekten und Interfaces einfach integrieren. Sie wurde für alle JSON-bezogenen Aufgaben verwendet.

## Query - Seite

### Projektor
Auf der Query - Seite werden die Events von einem Projektor - Objekt per Subscription aus der Queue entgegengenommen. 
Die asynchrone Variante wurde hier bewusst gewählt: Da davon ausgegangen werden kann, dass sich die Anzahl der Subscriber 
in unserem System sehr in Grenzen hält, ist dieses Pattern effizienter. Der Broker benachrichtigt die Clients, diese 
müssen keine Ressourcen auf polling verschwenden, und bekommen die Daten schnellstmöglich, nachdem sie verfügbar sind. 
Der Projektor deserialisiert die gesendeten Events und aktualisiert entsprechend das Query-Modell.

### Querymodell
Analog zum Domänenmodell besteht das Querymodell aus Hashmaps, die über ein von einer Containerklasse angebotenes 
Interface bedient werden können. Im Gegensatz zum Domänenmodell kommen hier allerdings zwei Maps zum Einsatz: 
Einmal werden VehicleDTOs nach Namen gespeichert, um die Abfrage der Fahrzeuge nach Namen zu ermöglichen. Auf dieser 
Datenstruktur können außerdem einfach alle Fahrzeuge abgefragt werden. Zusätzlich werden die VehicleDTOs in einer Map 
nach Position vorgehalten. Das ermöglicht zum einen die effiziente Abfrage der Fahrzeuge nach Position, da durch die 
nach Namen sortierte Datenstruktur sonst aufwendig iteriert werden müsste. Zudem kann so leicht festgestellt werden, 
ob sich auf einer Position mehrere Fahrzeuge befinden: Es wird eine Liste für die Position gespeichert, deren Länge 
ermittelt werden kann. Das Anlegen und Löschen der Listen kann durch die Containerklasse behandelt werden.

### Queries und QueryHandling
Die Daten im Querymodell können über Queries abgefragt werden. Um diese kümmert sich ein QueryHandler. Dieser kann 
die gewünschten Daten vom QueryModel abfragen. Da dieses, wie im vorherigen Abschnitt beschrieben, extra auf die 
vorgesehenen Queries optimiert wurde, ist hier wenig Kontrolllogik notwendig. Der QueryHandler gibt zurück, was das 
Querymodell liefert. Notfalls ist das Null, oder eine leere Liste.

## API
Zum Bedienen der Applikation gibt es eine REST-API. Sie nimmt sowohl Commands, als auch Queries entgegen. Sie 
konvertiert die Anfragen in die entsprechenden Objekte, und füttert damit den Command- bzw. QueryHandler. Entsprechende 
Ergebnisse, und auch Fehler, können als HTTP-Response zurückgegeben werden. Das ermöglicht es z.B., die Applikation 
zu Containerisieren und als Service bereitzustellen. 

## Testkonzept
Als Testkonzept für die Lösung wurde ein System-Test gewählt, die über JUnit betrieben wird. JUnit bietet sich als 
bewährtes Test framework an, um Tests jeglicher Art durchzuführen. Unittests erachte ich hier als wenig sinnvoll, da 
hierfür die Abhängigkeiten innerhalb der Applikation zu hoch sind. Einzelne Komponenten geben ihre Outputs direkt ans 
nächste System weiter und viele der Klassen existieren in einem Singleton-Kontext, was das Testen im Gegensatz 
zu erzeugten Objekten mit dependency-injection deutlich erschwert. Daher wird der Gesamtkontext getestet: Kommandos 
werden an den Command-Handler übergeben, und nachdem der Server etwas Zeit zur Verarbeitung hatte, wird getestet, 
ob eine Query das erwartete Ergebnis liefert. So können alle Kommandos und Queries einmal durchgetestet werden und 
man erhält einen guten Einblick, ob das System zuverlässig funktioniert.

# Aufgabe 3

## 3.1 und 3.2
Es könnte ein Counter eingeführt werden, der bei jedem Aufruf der moveVehicle Methode erhöht wird. So könnte einfach 
und effizient geprüft werden, wie oft ein Fahrzeug bewegt wurde. <br/>
Allerdings ändert sich diese Überlegung, wenn man die nächste Aufgabe betrachtet: Um festzustellen, ob sich ein 
Fahrzeug schonmal auf einer Position befunden hatte, muss eine Vergangenheit der Positionen vorgehalten werden. 
Das kann über eine einfache Liste realisiert werden. Deren Länge zu ermitteln ist kein großer rechnerischer Aufwand. 
Daher kann über die Länge der Bewegungshistorie herausgefunden werden, ob das Fahrzeug sich zum 20ten Mal bewegt und 
ob es daher entfernt werden muss. Die notwendigen Checks sollten im CommandHandler durchgeführt werden, da dieser auch 
die daraus folgenden Anpassungen am Domänenmodell vornehmen kann, und die entsprechenden Events erzeugt.

## 3.3
Es wäre möglich, das Domänenmodell dahingehend anzupassen, dass herausgefunden werden kann, ob sich bereits 
ein Fahrzeug auf der gewünschten Position befindet. Allerdings sind genau diese Informationen bereits im Query-Modell 
enthalten... es wäre also eine Doppelung der Daten, die Informationen auch noch im Domänenmodell vorzuhalten. 
Aus programmatischer Sicht macht es Sinn, die bereits vorhandene Schnittstelle zu nutzen.
Andererseits wurde in der Vorlesung und auf dem Aufgabenblatt die Wichtigkeit betont, Domänenmodell und Querymodell 
sauber zu trennen. Aus dem Commandhandler einen Aufruf an das Querymodell zu starten, wäre ein direkter Verstoß 
gegen dieses Prinzip. Es stellt sich außerdem die Frage, wie sich die Funktionalität verhält, wenn das Domänenmodell 
später aus dem Eventstore aufgebaut wird. <br/>
Um nicht gegen die diskutierten Prinzipien zu verstoßen, werden daher die Informationen in das Domänenmodell integriert. 
Da uns hier allerdings nur interessiert, ob ein Fahrzeug auf einer Position ist oder nicht, kann eine vereinfachte 
Datenstruktur gewählt werden, in der nur die Namen eines Fahrzeuges auf eine Position gemapped werden. So muss die 
Logik des Fahrzeugobjekts nicht dupliziert werden.