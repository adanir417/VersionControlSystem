import myenumpack.FilePaths
import java.io.File
import java.math.BigInteger
import java.security.MessageDigest




// ALL FILE OBJECTS WITH PATH
val vcsFile: File = File(FilePaths.VCS_FILE_PATH.path)
val fileRegisterIndexFile: File = File(FilePaths.FILE_REGISTER_INDEX_FILE_PATH.path)
val userNameConfigFile: File = File(FilePaths.USER_NAME_CONFIG_FILE_PATH.path)
val userChangesLogFile: File = File(FilePaths.USER_CHANGES_REGISTER_LOG_PATH.path)
val commitsFolder: File = File(FilePaths.COMMIT_FOLDER_PATH.path)


fun main(args: Array<String>) {
    fileCheckAndCreate()


    when (val command = if (args.isEmpty()) "--help" else args[0]) {
        //1
        "--help" -> showMenu()
        //2
        "config" -> userRegisterToConfigFile(userNameConfigFile, checkIndexSizeAndReturn(args))
        //3
        "add" -> addFileNameToIndexFile(fileRegisterIndexFile, checkIndexSizeAndReturn(args))
        //4
        "log" -> logToFile(userChangesLogFile)
        //5
        "commit" -> doCommit(
            userChangesLogFile,
            userNameConfigFile,
            fileRegisterIndexFile,
            checkIndexSizeAndReturn(args)
        )
        "checkout" -> doCheckOut(checkIndexSizeAndReturn(args))
        else -> println("'$command' is not a SVCS command.")
    }
}

fun fileCheckAndCreate() {
    // Directory Creation -- FOLDER
    if (!vcsFile.exists()) {
        vcsFile.mkdir()
    }
    if (!commitsFolder.exists()) {
        commitsFolder.mkdir()
    }


    // File Creation -- TXT,JPG etc...
    if (!userNameConfigFile.exists()) {
        userNameConfigFile.createNewFile()
    }

    if (!fileRegisterIndexFile.exists()) {
        fileRegisterIndexFile.createNewFile()
    }

    if (!userChangesLogFile.exists()) {
        userChangesLogFile.createNewFile()
    }
}

fun showMenu() {
    val commandsSVCS = """
These are SVCS commands:
config     Get and set a username.
add        Add a file to the index.
log        Show commit logs.
commit     Save changes.
checkout   Restore a file.
""".trim()

    println(commandsSVCS)

}

// 1
fun checkIndexSizeAndReturn(args: Array<String>): String? {
    return if (args.size == 2) {
        args[1]
    } else {
        null
    }
}


// 2
fun userRegisterToConfigFile(userConfig: File, userName: String?) {

    if (userName == null) {
        if (userConfig.readText().isEmpty()) {
            println("Please, tell me who you are.")
        } else {
            println("The username is ${userConfig.readText()}.")
        }
    } else {
        userConfig.writeText(userName)
        println("The username is $userName.")
    }

}

// 3
fun addFileNameToIndexFile(indexFile: File, fileName: String?) {

    indexFile.run {
        if (fileName == null) {
            if (readText().isEmpty()) {
                println("Add a file to the index.")
            } else {
                println("Tracked files:\n${readText()}")
            }

        } else {
            if (!File(fileName).exists()) {
                println("Can't find '$fileName'.")
            } else {
                println("The file '$fileName' is tracked.")
                readText().let {
                    writeText(
                        if (it.isEmpty()) {
                            fileName
                        } else {
                            it + "\n$fileName"
                        }
                    )
                }
            }
        }
    }
}


// 4
fun logToFile(logFile: File) {
    logFile.run {
        if (readText().isEmpty()) {
            println("No commits yet.")
        } else {
            println(logFile.readText())
            println()
        }
    }
}


// 5
fun doCommit(logFile: File, configFile: File, indexFile: File, option: String?) {
    if (option == null) {
        println("Message was not passed.")
    } else if (buildID(indexFile) == lastCommitID(logFile)) {
        println("Nothing to commit.")
    } else {
        val commitID = buildID(indexFile)
        logFile.readText().run {
            logFile.writeText("${commitInfo(configFile, commitID, option)}\n$this")
        }
        println("Changes are committed.")
        val commitDir = File("vcs/commits/$commitID")
        commitDir.mkdir()
        indexFile.readLines().forEach {
            File(it).copyTo(File("vcs/commits/$commitID/$it"))
        }
    }
}

// 6
fun doCheckOut(commitID: String?) {
    //val commitIDFile = File("${FilePaths.COMMIT_FOLDER_PATH.path}/$commitID/")
    //val lastCommitID = lastCommitID(userChangesLogFile)
    val myCommitListFile = File("${FilePaths.COMMIT_FOLDER_PATH.path}/")
    var checkCommitFileName = false
    myCommitListFile.walkTopDown().forEach { if (commitID == it.name) checkCommitFileName = true }
    if (commitID == null) println("Commit id was not passed.")
    else if (checkCommitFileName) {
        try {
            fileRegisterIndexFile.readLines().forEach {
                File("vcs/commits/$commitID/$it").copyTo(File(it),overwrite = true)
            }

            println("Switched to commit ${commitID}.")
        }catch (expc: Exception)
        {
            expc.printStackTrace()
        }
    }
    else {
        println("Commit does not exist.")
    }

}





fun lastCommitID(logFile: File): String {
    logFile.readLines().run {
        return if (this.isEmpty()) " " else this.first().substringAfter(' ')
    }
}

fun buildID(indexFile: File) = indexFile.run { bytesToHex(getHashFromReading(readLines().joinToString { File(it).readText() })) }
//fun buildID(indexFile: File) = bytesToHex(getHashFromReading(indexFile.readLines().joinToString { File(it).readText() }))
//fun buildID(indexFile: File) = md5(indexFile.readLines().joinToString { File(it).readText() })

/*
fun md5(input: String): String {
    val md = MessageDigest.getInstance("MD5")
    return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
}
*/

fun commitInfo(fileConfig: File, hashID: String, option: String) = """
            commit $hashID
            Author: ${fileConfig.readText()}
            $option
            
        """.trimIndent()

/*
fun getHashFromReading(byteArrayNeedToHash: ByteArray): ByteArray {
    val md = MessageDigest.getInstance("SHA-256")
    return md.digest(byteArrayNeedToHash)
}*/

fun getHashFromReading(hashString: String): ByteArray {
    val md = MessageDigest.getInstance("SHA-256")
    return md.digest(hashString.toByteArray())
}

fun bytesToHex(bytes: ByteArray): String {
    val sb = StringBuilder()
    for (i in bytes) {
        sb.append(String.format("%02x", i))
    }
    return sb.toString()
}