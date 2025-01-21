package org.example;

import com.espertech.esper.common.client.EPCompiled;
import com.espertech.esper.compiler.client.CompilerArguments;
import com.espertech.esper.compiler.client.EPCompileException;
import com.espertech.esper.compiler.client.EPCompiler;
import com.espertech.esper.compiler.client.EPCompilerProvider;
import com.espertech.esper.runtime.client.*;
import com.espertech.esper.common.client.configuration.*;


public class Main {

    public static void main(String[] args) throws EPCompileException, EPDeployException {

        // Configure the esper engine
        Configuration configuration = new Configuration();
        configuration.getCommon().addEventType(SensorEvent.class);
        configuration.getCommon().addEventType(SpeedDecreaseEvent.class);

        // EPL for calculating average speed per sensor
        String avgSpeedEPL = "@name('AverageSpeed') " +
                "insert into AverageSpeedEvent(sensorId, avgSpeed) " +
                "select sensorId, avg(speed) as avgSpeed " +
                "from SensorEvent#time_batch(30 sec) " +
                "where speed is not null " +
                "group by sensorId; ";

        // EPL for detecting speed decreases
        String speedDecreaseEPL = "@name('SpeedDecrease') " +
                "insert into SpeedDecreaseEvent(sensorId, prevAvgSpeed, currAvgSpeed) " +
                "select a.sensorId as sensorId, a.avgSpeed as prevAvgSpeed, b.avgSpeed as currAvgSpeed " +
                "from pattern [every a = AverageSpeedEvent -> " +
                "b = AverageSpeedEvent(a.sensorId = b.sensorId and b.avgSpeed < a.avgSpeed)];";

        // Prepare compiler
        EPCompiler compiler = EPCompilerProvider.getCompiler();
        CompilerArguments arguments = new CompilerArguments(configuration);
        EPCompiled epCompiled = compiler.compile(avgSpeedEPL + speedDecreaseEPL, arguments);

        // Deploy requests
        EPRuntime runtime = EPRuntimeProvider.getDefaultRuntime(configuration);
        runtime.initialize();
        EPDeployment deployment = runtime.getDeploymentService().deploy(epCompiled);

        // Hook kafka events
        Projector projector = new Projector(runtime);

        // Register listeners for results
        runtime.getDeploymentService()
                .getStatement(
                        deployment.getDeploymentId(),
                        "AverageSpeed"
                ).addListener(new SensorEventListener());
        runtime.getDeploymentService()
                .getStatement(
                        deployment.getDeploymentId(),
                        "SpeedDecrease"
                ).addListener(new SpeedDecreaseEventListener());
    }
}
