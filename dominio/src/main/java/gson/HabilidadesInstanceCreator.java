package gson;

import java.lang.reflect.Type;
import com.google.gson.InstanceCreator;

import casta.Casta;
import casta.Mago;

public class HabilidadesInstanceCreator implements InstanceCreator {
	public Casta createInstance(Type type) {
	    return new Mago();
	}
} 