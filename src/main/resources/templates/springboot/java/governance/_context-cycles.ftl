    @ArchTest
    static void each_bounded_context_root_must_be_free_of_cycles(JavaClasses classes) {
        var contexts = new TreeSet<>(${schemaTest}.detectContexts(classes));
        contexts.add(BASE_PACKAGE);
        for (var contextRoot : contexts) {
            slices()
                    .matching(contextRoot + ".(*)..")
                    .should()
                    .beFreeOfCycles()
                    .as("top-level package slices under " + contextRoot)
                    .allowEmptyShould(true)
                    .check(classes);
        }
    }
