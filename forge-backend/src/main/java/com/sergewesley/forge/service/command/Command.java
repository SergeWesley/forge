package com.sergewesley.forge.service.command;

public interface Command {
    String getName();

    String execute(String args);

    String getDescription();
}
