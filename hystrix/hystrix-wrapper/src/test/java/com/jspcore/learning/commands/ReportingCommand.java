package com.jspcore.learning.commands;

import java.util.concurrent.Future;

public interface ReportingCommand {
  String GROUP_KEY = "GROUP_KEY";
  String SUCCESS = "SUCCESS";
  String FALLBACK = "FALLBACK";

  boolean runWasCalled();

  boolean fallbackWasCalled();

  boolean hasCircuitBroken();

  String execute();

  Future<String> queue();

  boolean isExecutionComplete();
}
