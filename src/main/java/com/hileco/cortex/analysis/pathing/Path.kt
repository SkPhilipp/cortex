package com.hileco.cortex.analysis.pathing

import com.hileco.cortex.analysis.decompile.Break
import com.hileco.cortex.analysis.decompile.Continue
import java.math.BigInteger

data class Path(val entries: MutableList<PathEntry> = arrayListOf())

sealed class PathEntry

class Branch(val branch: BigInteger?) : PathEntry()

class Loop(val times: BigInteger?, val continueWith: Continue?, val exitWith: Break?) : PathEntry()
