package org.springframework.site.domain.tools.eclipse;

import java.util.List;

public class EclipseRelease {
	private final String name;
	private List<EclipsePackage> packages;

	public EclipseRelease(String name, List<EclipsePackage> packages) {
		this.name = name;
		this.packages = packages;
	}

	public String getName() {
		return name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		EclipseRelease that = (EclipseRelease) o;

		if (name != null ? !name.equals(that.name) : that.name != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return name != null ? name.hashCode() : 0;
	}

	public List<EclipsePackage> getPackages() {
		return packages;
	}
}
