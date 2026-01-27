package br.dev.purpura.gradle.versioning;

import org.gradle.api.Plugin;
import org.gradle.api.initialization.Settings;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.provider.ProviderFactory;

import javax.inject.Inject;

public abstract class VersioningPlugin implements Plugin<Settings>
{
    public static abstract class Extension
    {
        private final ProviderFactory providers;

        public abstract Property<String> getMajor ();

        public abstract Property<String> getMinor ();

        public abstract Property<String> getPatch ();

        public abstract ListProperty<String> getPreRelease ();

        public abstract ListProperty<String> getBuild ();

        @Inject
        public Extension (ProviderFactory providers)
        {
            this.providers = providers;
        }

        public Provider<String> getGitBranch ()
        {
            return providers.exec(exec -> {
                    exec.setCommandLine("git","rev-parse","--abbrev-ref","HEAD");
                    exec.setIgnoreExitValue(false);
                })
                .getStandardOutput().getAsText().map(String::trim);
        }

        public Provider<String> getGitCount ()
        {
            return providers.exec(exec -> {
                    exec.setCommandLine("git","rev-list","--count","HEAD");
                    exec.setIgnoreExitValue(false);
                })
                .getStandardOutput().getAsText().map(String::trim);
        }

        public Provider<String> getGitRevision ()
        {
            return providers.exec(exec -> {
                    exec.setCommandLine("git","rev-parse","--short","HEAD");
                    exec.setIgnoreExitValue(false);
                })
                .getStandardOutput().getAsText().map(String::trim);
        }

        public Provider<String> getDockerVersion ()
        {
            return providers.provider(() -> {
                var version = "%s.%s".formatted(
                    getMajor().orElse("0").get(),
                    getMinor().orElse("0").get()
                );
                var patch = getPatch()
                    .map(it -> "." + it)
                    .orElse("")
                    .get();
                var prerelease = getPreRelease()
                    .map(it -> {
                        if (it.isEmpty()) return "";
                        else return "-" + String.join(".",it);
                    })
                    .get();
                var build = getBuild()
                    .map(it -> {
                        if (it.isEmpty()) return "";
                        else return "-" + String.join(".",it);
                    })
                    .get();
                return version + patch + prerelease + build;
            });
        }

        public Provider<String> getSemanticVersion ()
        {
            return providers.provider(() -> {
                var version = "%s.%s".formatted(
                    getMajor().orElse("0").get(),
                    getMinor().orElse("0").get()
                );
                var patch = getPatch()
                    .map(it -> "." + it)
                    .orElse("")
                    .get();
                var prerelease = getPreRelease()
                    .map(it -> {
                        if (it.isEmpty()) return "";
                        else return "-" + String.join(".",it);
                    })
                    .get();
                var build = getBuild()
                    .map(it -> {
                        if (it.isEmpty()) return "";
                        else return "+" + String.join(".",it);
                    })
                    .get();
                return version + patch + prerelease + build;
            });
        }
    }

    @Override
    public void apply (Settings settings)
    {
        final var extension = settings.getExtensions().create("versioning",Extension.class);

        final var projectVersion = new Object() {
            public String toString () { return extension.getSemanticVersion().get(); }
        };

        settings.getGradle().beforeProject(project -> {
            project.setVersion(projectVersion);
        });
    }
}