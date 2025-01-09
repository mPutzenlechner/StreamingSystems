package org.example;

import com.espertech.esper.common.client.EventBean;
import com.espertech.esper.runtime.client.EPRuntime;
import com.espertech.esper.runtime.client.EPStatement;
import com.espertech.esper.runtime.client.UpdateListener;

import java.util.Arrays;

public class SensorEventListener implements UpdateListener {
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public void update(EventBean[] newEvents, EventBean[] oldEvents, EPStatement statement, EPRuntime runtime) {
        System.out.println(Arrays.toString(newEvents));
        if (newEvents != null) {
            for (EventBean newEvent : newEvents) {
                try {
                    int sensorId = (int) newEvent.get("sensorId");
                    if (newEvent.get("avgSpeed") == null) {
                        continue;
                    }
                    double avgSpeed = (double) newEvent.get("avgSpeed");
                    System.out.println("Sensor " + sensorId + ": Durchschnittsgeschwindigkeit = " + avgSpeed + " km/h");

                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }
}
