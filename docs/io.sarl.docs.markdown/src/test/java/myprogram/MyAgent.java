package myprogram;

import java.util.UUID;

import io.sarl.lang.core.Agent;
import io.sarl.lang.core.BuiltinCapacitiesProvider;
import io.sarl.lang.core.annotation.SarlSpecification;

@SuppressWarnings("all")
public class MyAgent extends Agent {

	public MyAgent(BuiltinCapacitiesProvider provider, UUID parentID, UUID agentID) {
		super(provider, parentID, agentID);
	}

	public MyAgent(UUID parentID, UUID agentID) {
		super(parentID, agentID);
	}

}
