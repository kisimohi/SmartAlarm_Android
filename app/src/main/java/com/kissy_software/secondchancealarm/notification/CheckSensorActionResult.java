package com.kissy_software.secondchancealarm.notification;

import com.kii.thingif.command.ActionResult;

public class CheckSensorActionResult extends ActionResult {

    @Override
    public String getActionName() {
        return "checkSensorAction";
    }
}