package com.aydindemir.agent.application.usecase;

import com.aydindemir.agent.application.command.CreateAgentCommand;
import com.aydindemir.agent.application.result.AgentResult;

public interface CreateAgentUseCase {

    AgentResult createAgent(CreateAgentCommand command);
}
