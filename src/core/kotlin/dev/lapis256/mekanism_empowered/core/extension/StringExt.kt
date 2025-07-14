package dev.lapis256.mekanism_empowered.core.extension


fun String.toTitleCase(): String {
    return this.split('_')
        .joinToString(" ") { word ->
            word.replaceFirstChar { char ->
                if (char.isLowerCase()) char.uppercase() else char.toString()
            }
        }
}
