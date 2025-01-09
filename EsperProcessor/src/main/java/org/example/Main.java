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

        // Define EPL requests
        String epl = "@name('AverageSpeed') " +
                "select sensorId, avg(speed) as avgSpeed " +
                "from SensorEvent#time_batch(30 sec) " +
                "where speed is not null and speed > 0 " +
                "group by sensorId";

        // Prepare compiler
        EPCompiler compiler = EPCompilerProvider.getCompiler();
        CompilerArguments arguments = new CompilerArguments(configuration);
        EPCompiled epCompiled = compiler.compile(epl, arguments);

        // Deploy requests
        EPRuntime runtime = EPRuntimeProvider.getDefaultRuntime(configuration);
        runtime.initialize();
        EPDeployment deployment = runtime.getDeploymentService().deploy(epCompiled);

        // Hook kafka events
        Projector projector = new Projector(runtime);


        // Register listener for requests
        EPStatement statement = runtime
                .getDeploymentService()
                .getStatement(
                        deployment.getDeploymentId(),
                        "AverageSpeed"
                );
        statement.addListener(new SensorEventListener());
    }
}
