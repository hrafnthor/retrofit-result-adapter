Releasing
========

1. Change the version in `gradle.properties` to a non-SNAPSHOT version.
2. `git commit -am "Release X.Y.Z."` (where X.Y.Z is the new version)
3. `git tag -a X.Y.Z -m "X.Y.Z"` (where X.Y.Z is the new version)
4. `git push & git push --tags`
5. Visit [Sonatype Nexus](https://s01.oss.sonatype.org/) and promote the artifact.
6. Update the `gradle.properties` to the next SNAPSHOT version.
7. `git commit -am "Prepare next development version."`
8. `git push`

If step 6 or 7 fails, drop the Sonatype repo, fix the problem, commit, and start again at step 5.
