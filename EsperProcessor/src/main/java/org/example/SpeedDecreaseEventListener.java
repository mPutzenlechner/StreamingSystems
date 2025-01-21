package org.example;

import com.espertech.esper.common.client.EventBean;
import com.espertech.esper.runtime.client.EPRuntime;
import com.espertech.esper.runtime.client.EPStatement;
import com.espertech.esper.runtime.client.UpdateListener;

public class SpeedDecreaseEventListener implements UpdateListener {
    @Override
    public void update(EventBean[] newData, EventBean[] oldData, EPStatement epStatement, EPRuntime epRuntime) {
        if (newData != null) {
            int sensorId = (int) newData[0].get("sensorId");
            double prevAvgSpeed = (double) newData[0].get("prevAvgSpeed");
            double currAvgSpeed = (double) newData[0].get("currAvgSpeed");
            System.out.println("Sensor " + sensorId + ": Geschwindigkeit gesunken von " + prevAvgSpeed + " m/s auf " + currAvgSpeed + " m/s");
        }
    }
}
