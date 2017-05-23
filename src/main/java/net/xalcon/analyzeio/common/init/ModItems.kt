package net.xalcon.analyzeio.common.init

import net.xalcon.analyzeio.AnalyzeIO
import net.xalcon.analyzeio.common.items.ItemHandheldAnalyzer

object ModItems
{
    val handheldAnalyzer: ItemHandheldAnalyzer = ItemHandheldAnalyzer()

    fun init()
    {
        //AnalyzeIO.PROXY.register(this.handheldAnalyzer)
    }
}