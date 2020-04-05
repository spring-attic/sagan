package sagan.projects;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.springframework.stereotype.Service;

@Service
public class ProjectPatchingService {

	public Project patch(Project newValue, Project valueToMutate) {
		return ObjectPatcher.patch(newValue, valueToMutate)
				.mutateIfDirty(Project::getRawOverview, Project::setRawOverview)
				.mutateIfDirty(Project::getRawBootConfig, Project::setRawBootConfig)
				.patchedValue();
	}

	// could be reused in other projects
	static class ObjectPatcher<T> {
		private T newValue;

		private T valueToMutate;

		private ObjectPatcher(T newValue, T valueToMutate) {
			this.newValue = newValue;
			this.valueToMutate = valueToMutate;
		}

		static <T> ObjectPatcher<T> patch(T newValue, T valueToMutate) {
			return new ObjectPatcher<>(newValue, valueToMutate);
		}

		<V> ObjectPatcher<T> mutateIfDirty(Function<T, V> getter, BiConsumer<T, V> modifyIfDirty) {
			V newValue = getter.apply(this.newValue);
			V valueToMutate = getter.apply(this.valueToMutate);
			if (isDirty(newValue, valueToMutate)) {
				// mutates the old object
				modifyIfDirty.accept(this.valueToMutate, newValue);
			}
			return this;
		}

		// syntactic sugar since new value got mutated all the way
		T patchedValue() {
			return this.valueToMutate;
		}

		private boolean isDirty(Object newValue, Object oldValue) {
			return newValue != null && !Objects.equals(newValue, oldValue);
		}
	}
}


