package com.aydindemir.agent.application.usecase;

import com.aydindemir.agent.application.query.GetAgentQuery;
import com.aydindemir.agent.application.result.AgentResult;

public interface GetAgentUseCase {

    AgentResult getAgent(GetAgentQuery query);
}
