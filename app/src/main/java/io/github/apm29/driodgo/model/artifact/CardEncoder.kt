package io.github.apm29.driodgo.model.artifact

import android.util.Base64
import java.lang.IllegalArgumentException
import java.util.regex.Pattern


// Basic Deck encoder

data class Deck(
    val heroes: MutableList<Card>,
    val cards: MutableList<Card>,
    val name: String
)

open class Card(
    val id: Int,
    val turn: Int,
    val count: Int
) {
    override fun toString(): String {
        return "Card{ id =$id,turn = $turn,count = $count}\n"
    }
}

private const val mCurrentVersion = 2
private const val PREFIX = "ADC"

object CardEncoder {

    val sm_nMaxBytesForVarUint32 = 5
    private const val HEADER_SIZE = 3
    //expects array("heroes" => array(id, turn), "cards" => array(id, count), "name" => name)
    //	signature cards for heroes SHOULD NOT be included in "cards"
    fun encodeDeck(deckContents: Deck): String {
        val bytes = encodeBytes(deckContents)
        return encodeBytesToString(bytes)
    }

    private fun encodeBytesToString(
        bytes: ArrayList<Byte>
    ): String {
        val deckString = PREFIX + String(Base64.encode(bytes.toByteArray(), Base64.DEFAULT))
        return deckString.replace("/", "-").replace("=", "_")
    }


    /**
     * deck -> byteArray
     */
    fun encodeBytes(deckContents: Deck): ArrayList<Byte> {
        /**
         * 将hero,card按id排序
         */
        val comparator = Comparator<Card> { o1, o2 ->
            if (o1.id <= o2.id) -1 else 1
        }
        deckContents.cards.sortWith(comparator)
        deckContents.heroes.sortWith(comparator)

        //hero数量
        val heroCount = deckContents.heroes.size
        //所有卡牌
        val allCards: List<Card> = deckContents.heroes + deckContents.cards

        //结果数据
        val bytes = arrayListOf<Byte>()

        //第一位 version
        val version = mCurrentVersion shl 4 or extractNBitWithCarry(heroCount, 3)
        bytes.add(version.toByte())

        //第二位 dummyCheckSum
        val dummyCheckSum = 0
        val checkSumBytes = bytes.size
        bytes.add(dummyCheckSum.toByte())

        //第三部分 name
        //需要去除html标签
        var name = deckContents.name.deleteHtml()
        //缩短长度
        val originLength = name.toByteArray().size
        while (originLength > 63) {
            var amountToTrim = (originLength - 63) / 4
            amountToTrim = if (amountToTrim > 1) amountToTrim else 1
            name = name.substring(0, name.length - amountToTrim)
        }
        //第一位,截断到63字节之内的长度
        val nameLen = name.toByteArray().size
        bytes.add(nameLen.toByte())
        //英雄个数
        addRemainingNumberToBuffer(heroCount, 3, bytes)

        var prevCardId = 0
        //依次添加hero卡牌
        deckContents.heroes.forEachIndexed { index, card ->
            if (card.turn == 0) {
                throw  IllegalArgumentException("回合数不可为0")
            }
            addCardToBuffer(card.turn, card.id - prevCardId, bytes)
            prevCardId = card.id
        }

        prevCardId = 0

        deckContents.cards.forEachIndexed { index, card ->
            if (card.count == 0) {
                throw  IllegalArgumentException("count不可为0")
            }
            if (card.id <= 0) {
                throw  IllegalArgumentException("id不可为0")
            }

            addCardToBuffer(card.count, card.id - prevCardId, bytes)
            prevCardId = card.id
        }

        val preStringBytesCount = bytes.size

        name.toByteArray().forEach {
            bytes.add(it)
        }


        val unFullChecksum = computeCheckSum(bytes, preStringBytesCount - HEADER_SIZE)
        val unSmallChecksum = unFullChecksum and 0x0ff

        bytes[checkSumBytes] = unSmallChecksum.toByte()

        return bytes
    }

    private fun computeCheckSum(bytes: ArrayList<Byte>, unNumBytes: Int): Int {
        var unChecksum = 0
        for (unAddCheck in HEADER_SIZE until (unNumBytes + HEADER_SIZE)) {
            val byte = bytes[unAddCheck]
            unChecksum += byte.toInt()
        }
        return unChecksum
    }

    /**
     * unCount:count/turn
     * unValueP:id
     */
    private fun addCardToBuffer(unCount: Int, unValueP: Int, bytes: ArrayList<Byte>) {
        val countBytesStart = bytes.size
        val knFirstByteMaxCount = 0x03
        val bExtendedCount = (unCount - 1) >= knFirstByteMaxCount
        val unFirstByteCount = if (bExtendedCount) knFirstByteMaxCount else (unCount - 1)
        var unFirstByte = unFirstByteCount shl 6
        unFirstByte = unFirstByte or extractNBitWithCarry(unValueP, 5)

        bytes.add(unFirstByte.toByte())

        addRemainingNumberToBuffer(unValueP, 5, bytes)

        if (bExtendedCount) {
            addRemainingNumberToBuffer(unCount, 0, bytes)
        }

        val countBytesEnd = bytes.size

        if (countBytesEnd - countBytesStart > 11) {
            throw IllegalArgumentException("add 11?")
        }
    }

    private fun extractNBitWithCarry(value: Int, byteNum: Int): Int {
        val unLimitBit = 1 shl byteNum
        var unResult = value and unLimitBit - 1
        if (value >= unLimitBit) {
            unResult = unResult or unLimitBit
        }
        return unResult
    }

    private fun addRemainingNumberToBuffer(unValueP: Int, unAlreadyWrittenBits: Int, bytes: ArrayList<Byte>) {
        var unValue = unValueP shr unAlreadyWrittenBits
        var unNumBytes = 0
        while (unValue > 0) {
            val unNextByte = extractNBitWithCarry(unValue, 7)
            unValue = unValue shr 7
            bytes.add(unNextByte.toByte())
            unNumBytes++
        }
    }
}

/**
 * 定义script的正则表达式
 */
private const val REGEX_SCRIPT = "<script[^>]*?>[\\s\\S]*?<\\/script>"
/**
 * 定义style的正则表达式
 */
private const val REGEX_STYLE = "<style[^>]*?>[\\s\\S]*?<\\/style>"
/**
 * 定义HTML标签的正则表达式
 */
private const val REGEX_HTML = "<[^>]+>"
/**
 * 定义空格回车换行符
 */
private const val REGEX_SPACE = "\\s*|\t|\r|\n"

fun delHTMLTag(htmlStr: String): String {
    var copy = htmlStr
    // 过滤script标签
    val pScript = Pattern.compile(REGEX_SCRIPT, Pattern.CASE_INSENSITIVE)
    val mScript = pScript.matcher(copy)
    copy = mScript.replaceAll("")
    // 过滤style标签
    val pStyle = Pattern.compile(REGEX_STYLE, Pattern.CASE_INSENSITIVE)
    val mStyle = pStyle.matcher(copy)
    copy = mStyle.replaceAll("")
    // 过滤html标签
    val pHtml = Pattern.compile(REGEX_HTML, Pattern.CASE_INSENSITIVE)
    val mHtml = pHtml.matcher(copy)
    copy = mHtml.replaceAll("")
    // 过滤空格回车标签
    val pSpace = Pattern.compile(REGEX_SPACE, Pattern.CASE_INSENSITIVE)
    val mSpace = pSpace.matcher(copy)
    copy = mSpace.replaceAll("")
    return copy.trim { it <= ' ' } // 返回文本字符串
}

fun String.deleteHtml(): String {
    return delHTMLTag(this)
}


object CardDecoder {

    private var currentByteIndex = 0
    private var bytes: IntArray = intArrayOf()

    fun parseDeck(deckCode: String): Deck {
        bytes = decodeDeckString(deckCode)
        return parseBytesInternal()
    }

    private fun parseBytesInternal(): Deck {
        currentByteIndex = 0
        val totalBytes = bytes.size
        val versionAndHero = bytes[currentByteIndex++]

        val version = versionAndHero.toInt() shr 4
        if (version != mCurrentVersion) {
            throw IllegalArgumentException("并非当前版本卡组")
        }
        val checkSum = bytes[currentByteIndex++]

        var stringLength = 0

        if (version > 1) {
            stringLength = bytes[currentByteIndex++]
        }
        val totalCardBytes = totalBytes - stringLength

        //grab the string size
        var computedCheckSum = 0
        var i = currentByteIndex
        while (i < totalCardBytes) {
            computedCheckSum += bytes[i]
            i++
        }
        val masked = computedCheckSum and 0x0FF
        if (checkSum != masked) {
            throw IllegalArgumentException("验证位错误")
        }

        val heroCount = Mask(0)
        if(!readVarEncodedUint32(versionAndHero, 3, totalCardBytes,heroCount)){
            throw IllegalArgumentException("heroCount")
        }
        //heroes
        val heroes = arrayListOf<Card>()

        var prevCardBase = 0
        var currentHero = 0
        while (currentHero < heroCount.out) {
            val card = readSerializedCard(totalCardBytes, prevCardBase, true)
            //update our previous card before we do the remap, since it was encoded without the remap
            prevCardBase = card.id
            currentHero++
            heroes.add(card)
        }

        val cards = arrayListOf<Card>()
        prevCardBase = 0
        while (currentByteIndex < totalCardBytes) {
            val card = readSerializedCard(totalCardBytes, prevCardBase, false)
            prevCardBase = card.id

            cards.add(card)
        }
        //-72, 2, 1, 1, 6, -115, 61, -72, 2, 10, 8, -14, 1, 4, 1, 1, -127, 4, -63, 5, 1, 3

        val nameBytes = arrayListOf<Byte>()
        bytes.forEachIndexed { index, byte ->
            if (index >= currentByteIndex) {
                nameBytes.add(byte.toByte())
            }
        }

        val name = String(nameBytes.toByteArray()).deleteHtml()

        return Deck(
            heroes, cards, name
        )
    }

    private fun readSerializedCard(
        indexEnd: Int,
        nPrevCardBase: Int,
        isHero: Boolean
    ): Card {

        if (currentByteIndex > indexEnd) {
            throw IllegalArgumentException("index 错误")
        }

        //header contains the count (2 bits), a continue flag, and 5 bits of offset data. If we have 11 for the count bits we have the count
        //encoded after the offset
        val nHeader = bytes[currentByteIndex++]
        val bHasExtendedCount = ((nHeader.toInt() shr 6) == 0x03)
        //read in the delta, which has 5 bits in the header, then additional bytes while the value is set
        val nCardDelta = Mask(0)
        if ( !readVarEncodedUint32(nHeader.toInt(), 5, indexEnd,nCardDelta)){
            throw  IllegalArgumentException("card delta")
        }
        val nOutID = nPrevCardBase + nCardDelta.out

        //now parse the count if we have an extended count
        val nOutCount = if (bHasExtendedCount) {
            val nOut = Mask(0)
            if(!readVarEncodedUint32(0, 0, indexEnd,nOut)){
                throw IllegalArgumentException("outCount")
            }
            nOut.out
        } else {
            //the count is just the upper two bits + 1 (since we don't encode zero)
            (nHeader.toInt() shr 6) + 1
        }

        return if (isHero) Card(nOutID, nOutCount, 1) else Card(nOutID, 1, nOutCount)
    }

    private fun readVarEncodedUint32(
        baseValue: Int,
        baseBit: Int,
        indexEnd: Int,
        outValue:Mask
    ): Boolean {
        var deltaShift = 0
        val mask = Mask(0)
        if ((baseBit == 0) || readBitsChunk(baseValue, baseBit, deltaShift, mask)) {
            deltaShift += baseBit
            while (true) {
                if (currentByteIndex > indexEnd) {
                    throw IllegalArgumentException("index 错误")
                }
                val nextByte = bytes[currentByteIndex++]
                if (!readBitsChunk(nextByte.toInt(), 7, deltaShift, mask)) {
                    break
                }
                deltaShift += 7
            }
        }
        outValue.out = mask.out
        return true
    }

    data class Mask(
        var out: Int
    )

    private fun readBitsChunk(
        chunk: Int,
        numBit: Int,
        currentShift: Int,
        mask: Mask
    ): Boolean {

        val continueBit = 1 shl numBit
        val newBits = chunk and (continueBit - 1)
        mask.out = mask.out or (newBits shl currentShift)
        return (chunk and continueBit) != 0
    }

    private fun decodeDeckString(deckCode: String): IntArray {
        if (deckCode.substring(0, PREFIX.length) != PREFIX) {
            throw IllegalArgumentException("非法的deck code开头")
        }

        val noPrefixCode = deckCode.substring(PREFIX.length)

        val raw = noPrefixCode.replace("-", "/").replace("_", "=")
        return Base64.decode(raw.toByteArray(), Base64.DEFAULT).map {
            if (it < 0){
                (it+256)
            }else{
                it.toInt()
            }
        }.toIntArray()
    }
}


