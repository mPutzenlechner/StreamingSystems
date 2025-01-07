package org.example;

import com.espertech.esper.common.client.EPCompiled;
import com.espertech.esper.compiler.client.CompilerArguments;
import com.espertech.esper.compiler.client.EPCompileException;
import com.espertech.esper.compiler.client.EPCompiler;
import com.espertech.esper.compiler.client.EPCompilerProvider;
import com.espertech.esper.runtime.client.*;
import com.espertech.esper.common.client.configuration.*;
import com.espertech.esper.common.client.configuration.common.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Main {

    public static void main(String[] args) throws EPCompileException, EPDeployException {
        // Configure the esper engine
        Configuration configuration = new Configuration();
        configuration.getCommon().addEventType(SensorEvent.class);

        // Define EPL requests
        String epl = "@name('AverageSpeed') " +
                "select sensorId, avg(speed) as avgSpeed " +
                "from SensorEvent#time_batch(1 min) " +
                "where speed is not null " +
                "group by sensorId";

        // Prepare compiler
        EPCompiler compiler = EPCompilerProvider.getCompiler();
        CompilerArguments arguments = new CompilerArguments(configuration);
        EPCompiled epCompiled = compiler.compile(epl, arguments);

        // Deploy requests
        EPRuntime runtime = EPRuntimeProvider.getDefaultRuntime(configuration);
        runtime.initialize();
        EPDeployment deployment = runtime.getDeploymentService().deploy(epCompiled);

        // Register listener for requests
        EPStatement statement = runtime
                .getDeploymentService()
                .getStatement(
                        deployment.getDeploymentId(),
                        "AverageSpeed"
                );
        statement.addListener(new SensorEventListener());


        // Simulate incoming events
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
        df.setTimeZone(tz);

        runtime.getEventService().sendEventBean(new SensorEvent(df.format(new Date()), 2,
                /* new double[]{ */31.3 /*, 22.3, 21.5}*/), "SensorEvent"
        );
        runtime.getEventService().sendEventBean(new SensorEvent(df.format(new Date()), 1,
                /* new double[]{ */1.4 /*, 11.2, 71.4}*/), "SensorEvent"
        );
        runtime.getEventService().sendEventBean(new SensorEvent(df.format(new Date()), 2,
                /* new double[]{ */36.3 /*, 32.2, 20.5}*/), "SensorEvent"
        );
        runtime.getEventService().sendEventBean(new SensorEvent(df.format(new Date()), 3,
                /* new double[]{ */31.9 /*, 22.3, 10.0}*/), "SensorEvent"
        );
    }
}
