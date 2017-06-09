package com.ivieleague.kotlin.server

sealed class ServerType<KOTLIN> {
    object TBoolean : ServerType<Boolean>()

    object TByte : ServerType<Byte>()
    object TShort : ServerType<Short>()
    object TInt : ServerType<Int>()
    object TLong : ServerType<Long>()

    object TFloat : ServerType<Float>()
    object TDouble : ServerType<Double>()

    object TString : ServerType<String>()
    class TPointer(val table: Table) : ServerType<Pair<Table, String>>()
    class TListPointers(val table: Table) : ServerType<Pair<Table, List<String>>>()
}