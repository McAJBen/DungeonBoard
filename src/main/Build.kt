package main

object Build {
    private const val MAJOR = 0
    private const val MINOR = 0
    private const val PATCH = 0
    private const val RELEASE = false

    val versionText: String
        get() {
            @Suppress("ConstantConditionIf")
            return "v$MAJOR.$MINOR.$PATCH${if (RELEASE) "" else "-dev"}"
        }
}