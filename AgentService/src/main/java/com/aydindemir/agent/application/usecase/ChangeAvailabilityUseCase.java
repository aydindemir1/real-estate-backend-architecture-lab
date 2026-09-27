package com.aydindemir.agent.application.usecase;

import com.aydindemir.agent.application.command.ChangeAvailabilityCommand;
import com.aydindemir.agent.application.result.AgentResult;

public interface ChangeAvailabilityUseCase {

    AgentResult changeAvailability(ChangeAvailabilityCommand command);
}
