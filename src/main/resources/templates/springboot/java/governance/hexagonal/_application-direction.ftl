    @ArchTest
    static final ArchRule application_must_not_depend_on_adapters =
            noClasses()
                    .that()
                    .resideInAnyPackage(familyPattern(FAMILY_APPLICATION))
                    .should()
                    .dependOnClassesThat()
                    .resideInAnyPackage(familyPattern(FAMILY_ADAPTER))
                    .allowEmptyShould(true);
