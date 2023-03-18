package myenumpack

enum class CommandDescription (val description: String) {
    CONFIG_HELP_DESCRIPTION ("Get and set a username."),
    ADD_HELP_DESCRIPTION ( "Add a file to the index."),
    LOG_HELP_DESCRIPTION ( "Show commit logs." ),
    COMMIT_HELP_DESCRIPTION ( "Save changes." ),
    CHECKOUT_HELP_DESCRIPTION ("Restore a file.");
}