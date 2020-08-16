package tech.acruxsolutions.atomnote.miscellaneous

data class Operation(val textId: Int, val drawableId: Int, val operation: () -> Unit)