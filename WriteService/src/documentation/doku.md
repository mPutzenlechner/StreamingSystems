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
werden an die Schnittstellen der REST-API gesendet. Anschließend wird einige Sekunden gewartet, damit die Events 
auf der Query-Seite ankommen können. Danach wird der Stand der Query-Seite abgefragt, um die korrekte Ausführung zu 
überprüfen. Um dem normalen Betrieb nicht in die Quere z kommen, wird ein spezielles Testobjekt definiert, welches 
nach den Tests auch wieder entfernt wird.
Der Test über die API-Schnittstelle ermöglicht einen Test des Gesamtsystems, auch in einem verteilten Szenario. 
Die Write-Seite und Query-Seite können getrennt werden und in verschiedenen Containern liegen, vielleicht sogar skaliert 
werden. Solange die API definiert und erreichbar ist, kann das System getestet werden.

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
Andererseits wurde in der Vorlesung und auf dem Aufgabenblatt die Wichtigkeit betont, Domänenmodell und Query-Modell 
sauber zu trennen. Aus dem Command handler einen Aufruf an das Query-Modell zu starten, wäre ein direkter Verstoß 
gegen dieses Prinzip. Es stellt sich außerdem die Frage, wie sich die Funktionalität verhält, wenn das Domänenmodell 
später aus dem Eventstore aufgebaut wird. <br/>
Um nicht gegen die diskutierten Prinzipien zu verstoßen, werden daher die Informationen in das Domänenmodell integriert. 
Da uns hier allerdings nur interessiert, ob ein Fahrzeug auf einer Position ist oder nicht, kann eine vereinfachte 
Datenstruktur gewählt werden, in der nur die Namen eines Fahrzeuges auf eine Position gemapped werden. So muss die 
Logik des Fahrzeugobjekts nicht dupliziert werden.

# Aufgabe 4

Die Aufgabe 4 wurde nicht bearbeitet, da sie für Einzel-arbeitende erspart wurde.

# Aufgabe 5

Durch die zuvor beschriebene Struktur, in der der EventStoreService von den anderen Systemen getrennt ist, ließ sich 
dieser recht einfach von ActiveMQ in Kafka umbauen. So musste nur eine Klasse ersetzt werden, die commands und events 
konnten gleich bleiben. Auf dem Kafka-Server gibt es ein Topic, hier werden die Events im JSON-Format eingestellt. 
Die Rekonstruktion des Domänenmodells auf Write-Seite funktioniert über einen Replay aller Events, die auf Client-Seite 
nach den relevanten Events gefiltert und verarbeitet werden. Das ist suboptimal: um das aktuelle Fahrzeug auf einer 
Position herauszufinden, dauert die Anfrage ca. 1.4 Sekunden mit relativ wenigen Events. Das könnte man optimieren, 
indem schon bei der Abfrage der Events von Kafka eine entsprechende Filterung angelegt wird, es scheint aber in Kafka 
keine Möglichkeit zu geben das zu machen.
Auf Read-Seite bestehen diese Probleme nicht, da hier der aktuelle Stand immer noch progressiv aufgebaut wird.
Bei Start des Programmes wird einmal die gesamte Historie abgefragt, um den aktuellen Stand herzustellen. Da diese 
Aktion einmal pro Programmstart ausgeführt wird, ist dies ein rechtfertigbarer Aufwand.

Beim Vergleich mit anderen Lösungen kam eine interessante Diskussion über die Schachtelung von Events auf. 
Man stelle sich folgendes Szenario vor: auf einer Position, auf die ein Fahrzeug sich bewegt, ist schon ein Fahrzeug. 
Nach Definition muss dieses nun entfernt werden. In meiner Lösung wird das wie folgt gelöst: innerhalb des 
moveVehicle Handlers wird der removeVehicle Handler aufgerufen, und bekommt die entsprechenden Parameter mitgegeben. 
Ein berechtigter Einwand war, dass so keine Konsistenz gewährleistet wird. Schlägt beispielsweise die Ausführung des 
remove-Commands fehl, würde der move-Command trotzdem weiterlaufen. Das kann zu einer Dateninkonsistenz führen.
Für unsere Zwecke war meine Argumentation, dass eine Mitigation dieses Problem unverhältnismäßig viel Arbeit mit sich 
bringen würde. Außerdem sollte dieses Problem als Vergleich mit einer anderen Lösung bestehen bleiben.

# Aufgabe 6

Die Implementierung des Datengenerators war unproblematisch. Die entsprechenden Parameter können als Konstanten 
definiert werden. Auf deren Basis werden Zufallswerte generiert und in einen String im entsprechenden Format zusammengefügt. 
Dieser wird in einem eigenen Kafka-Topic eingestellt, von wo sie abgeholt werden können. Auch die geforderten negativen 
Werte und leeren Nachrichten werden durch die Zufallswerte abgedeckt.

# Aufgabe 7

Die Beam-Pipeline kann sich über die integrierte Kafka-Unterstützung die Nachrichten aus dem definierten Topic 
abholen, die Generierung der Beam-Werte geschieht automatisch. Diese werden in einer PCollection gesammelt. 
Anschließend durchlaufen sie einen Filter, in dem die einzelnen Nachrichtenkomponenten voneinander getrennt werden, 
die ungültigen Nachrichten aussortiert bzw. ignoriert werden. Anschließend werden die Nachrichten in Windows eingeteilt 
und die Einheit umgerechnet.
Als sinnvolle Darstellungsmöglichkeit ist hier eine textbasierte Ausgabe implementiert. Die Darstellung in Graphen 
würde sich hier auch anbieten.

# Aufgabe 8

## Umsetzung

Für Esper existiert wohl auch ein Plugin, mit dem Kafka Nachrichten importiert werden können. Da ich dieses und auch 
passende Dokumentation nicht gefunden habe, entschied ich mich für einen selbstgebauten Konnektor. Zwar ließ die 
Aufgabenstellung offen, ob überhaupt die Daten aus der Kafka-Queue verwendet werden sollen. Dies erschien aber 
sinnvoll, schließlich war der Generator schon implementiert und die Kafka-Queue aufgesetzt. Das ermöglicht außerdem 
den direktesten Vergleich mit der Beam-Pipeline aus Aufgabe 7.
Als Konnektor habe ich daher den Kafka-Konsumenten aus Aufgabe 5 übernommen und modifiziert, sodass er die Events 
aus dem passenden Topic abholt, und diese als Esper-Events im Esper-System raised.
Dort kann mithilfe einer EPL-Abfrage die Validität der Daten überprüft und gleichzeitig die Durchschnittsgeschwindigkeit 
über einen Zeitraum von 30 Sekunden berechnet werden. Diese wird, ihrem erzeugenden Sensor zugeordnet, in einem neuen... 
"Kanal"? veröffentlicht. Auf diesem wird eine zweite EPL-Abfrage aufgesetzt, die für jeden Sensor erkennt, ob seit der 
letzten Eventveröffentlichung, also in den letzten 30 Sekunden, ein Geschwindigkeitsabfall vorliegt, der für eine 
Stau-Bildung sprechen würde. Ist das der Fall, wird wieder ein neues Event erzeugt, das auf diesen Umstand hinweist.

## Teststrategie

Ein sinnvoller Test wäre es, per Unittest Testdaten in das System einzufügen und den Output zu überprüfen. 
Beispiele für sinnvolle Tests wären:
- Funktioniert die Konvertierung von m/s zu km/h?
- werden mehrere Geschwindigkeitswerte pro Nachricht sinnvoll zusammengefasst?
- Wird ein Geschwindigkeitsabfall korrekt erkannt und mitgeteilt?
- Werden falsche Werte und fehlende Werte korrekt behandelt?
Um diese Tests durchzuführen, könnte ein Testproducer geschrieben werden, der die entsprechenden Werte in Kafka 
einstellt. So würde das gesamte System, inklusive der Datenaufnahme getestet werden. Alternativ könnte man sich 
entscheiden, Kafka auszulassen. Dann würden die Testwerte beispielsweise per JUnit direkt in das System eingefügt.
