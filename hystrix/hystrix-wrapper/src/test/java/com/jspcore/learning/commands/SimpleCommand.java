package com.jspcore.learning.commands;

public class SimpleCommand extends DelayedCommand {
  public SimpleCommand(String commandKey) {
    this(GROUP_KEY, commandKey);
  }

  public SimpleCommand(String groupKey, String commandKey) {
    super(groupKey, commandKey, 0);
  }
}
