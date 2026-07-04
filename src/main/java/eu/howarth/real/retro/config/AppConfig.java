package eu.howarth.real.retro.config;

import io.smallrye.config.ConfigMapping;

/**
 * Type-safe view over the {@code app.*} properties. Quarkus binds this interface
 * at build time and exposes it as an injectable bean; method names map to
 * kebab-case keys (e.g. {@code gitSha()} -> {@code app.git-sha}).
 */
@ConfigMapping(prefix = "app")
public interface AppConfig {

    String version();

    String gitSha();

    String builtAt();
}
