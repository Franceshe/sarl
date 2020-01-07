package io.sarl.activeannotation;

import java.util.UUID;

import io.sarl.lang.core.Agent;
import io.sarl.lang.core.BuiltinCapacitiesProvider;
import io.sarl.lang.core.annotation.SarlSpecification;

@SuppressWarnings("all")
public class MyDelegate implements SomeInterface {
	public int function(String param) {
		return 0;
	}
}
