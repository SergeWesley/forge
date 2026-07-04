package com.sergewesley.forge.service.command.api;

public interface Command {
    String getName();

    String execute(String args);

    String getDescription();
}
