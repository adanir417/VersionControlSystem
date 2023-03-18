package myenumpack

enum class FilePaths (val path: String) {
    VCS_FILE_PATH ( "vcs" ),
    FILE_REGISTER_INDEX_FILE_PATH ( "vcs/index.txt"),
    USER_NAME_CONFIG_FILE_PATH ( "vcs/config.txt"),
    USER_CHANGES_REGISTER_LOG_PATH ( "vcs/log.txt"),
    COMMIT_FOLDER_PATH ( "vcs/commits"),

}