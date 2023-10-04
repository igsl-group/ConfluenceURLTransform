package com.igsl.export.dc;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.igsl.config.Config;

public abstract class ObjectExport {
	protected Path getOutputPath(Config config) throws IOException {
		return Paths.get(config.getOutputDirectory().toFile().getAbsolutePath(), this.getClass().getSimpleName() + ".csv");
	}
	public abstract Path exportObjects(Config config) throws Exception;
}
