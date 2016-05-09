package com.android.inputmethod.pinyin;

import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.CompletionInfo;

import java.util.List;
import java.util.Vector;

/**
 * Created by lvhonghe on 16/5/5.
 */
public class GooglePinyinIME {

    private  GooglePinyinIME sGooglePinyinIME;
    private Context mContext;

    private DecodingInfo mDecInfo;
//
//    public int mTotalChoicesNum;
//
//    public List<String> mCandidatesList = new Vector<String>();
//
//    public Vector<Integer> mPageStart = new Vector<Integer>();
//
//    public Vector<Integer> mCnToPage = new Vector<Integer>();
//
//    public int mPosDelSpl = -1;
//
//    public boolean mIsPosInSpl;

    public GooglePinyinIME(Context context) {
        this.mContext = context;
        mDecInfo = new DecodingInfo(context);
    }


    private void init() {

    }

    public void setText(CharSequence text) {
        //// TODO: 16/5/6 回调 界面显示以及处理字符
    }

    public void processChar(char keyToProcess) {
        processSurfaceChange(keyToProcess);
    }

    public  void processDel() {

    }

    public void processSpace() {

    }


    //三个状态 input predict composing


    /**
        process Input
     */
    private boolean processStateInput(int keyChar, int keyCode, KeyEvent event) {
        return processSurfaceChange(keyChar);
    }

    /**
     * 通过拼音进行词库查询
     *
     */

    private boolean processSurfaceChange(int keyChar) {
        if(mDecInfo.isSplStrFull())
            return true;
        mDecInfo.addSplChar((char)keyChar, false);
        chooseAndUpdate(-1);
        return true;
    }

    /**
     * 选择候选词，并根据条件是否进行下一步的预报
     */

    private void chooseAndUpdate(int canId) {
        //解析候选词
        mDecInfo.chooseDecodingCandidate(canId);

        //根据拼音进行预测
        mDecInfo.choosePredictChoice(canId);

        if(mDecInfo.getComposingStr().length() > 0) {
            String resultStr;
            resultStr = mDecInfo.getComposingStrActivePart();
            //Todo ui相关
//            if(mDecInfo.getSplStrDecodedLen() == 0) {
//                changeToStateComposing(true);
//            } else {
//                changeToStateInput(true);
//            }
        } else  {
            resetToIdleState();
        }
    }


    /**
     * 重置为为输入数据状态
     */
    private void resetToIdleState() {
        mDecInfo.reset();
        //界面的展现回调
    }

    private void onChoiceTouched(int activeCandNo) {

    }

    /**
     * 词库解码操作对象
     *
     * @ClassName DecodingInfo
     * @author keanbin
     */
    public class DecodingInfo {
        /**
         * Maximum length of the Pinyin string
         * 最大的字符串的长度，其实只有27，因为最后一位为0，是mPyBuf[]的长度
         */
        private static final int PY_STRING_MAX = 28;

        /**
         * Maximum number of candidates to display in one page. 一页显示候选词的最大个数
         */
        private static final int MAX_PAGE_SIZE_DISPLAY = 10;

        /**
         * Spelling (Pinyin) string. 拼音字符串
         */
        private StringBuffer mSurface;

        /**
         * Byte buffer used as the Pinyin string parameter for native function
         * call. 字符缓冲区作为拼音字符串参数给本地函数调用，它的长度为PY_STRING_MAX，最后一位为0
         */
        private byte mPyBuf[];

        /**
         * The length of surface string successfully decoded by engine.
         * 成功解码的字符串长度
         */
        private int mSurfaceDecodedLen;

        /**
         * Composing string. 拼音字符串
         */
        private String mComposingStr;

        /**
         * Length of the active composing string. 活动的拼音字符串长度
         */
        private int mActiveCmpsLen;

        /**
         * Composing string for display, it is copied from mComposingStr, and
         * add spaces between spellings.
         *
         * 显示的拼音字符串，是从mComposingStr复制过来的，并且在拼写之间加上了空格。
         **/
        private String mComposingStrDisplay;

        /**
         * Length of the active composing string for display. 显示的拼音字符串的长度
         */
        private int mActiveCmpsDisplayLen;

        /**
         * The first full sentence choice. 第一个完整句子，第一个候选词。
         */
        private String mFullSent;

        /**
         * Number of characters which have been fixed. 固定的字符的数量
         */
        private int mFixedLen;

        /**
         * If this flag is true, selection is finished. 是否选择完成了？
         */
        private boolean mFinishSelection;

        /**
         * The starting position for each spelling. The first one is the number
         * of the real starting position elements. 每个拼写的开始位置，猜测：第一个元素是拼写的总数量？
         */
        private int mSplStart[];

        /**
         * Editing cursor in mSurface. 光标的位置
         */
        private int mCursorPos;

        /**
         * Remote Pinyin-to-Hanzi decoding engine service. 解码引擎远程服务
         */
        private PinyinDecoderService mPinyinDecoderService;
        /**
         * The complication information suggested by application. 应用的并发建议信息
         */
        private CompletionInfo[] mAppCompletions;

        /**
         * The total number of choices for display. The list may only contains
         * the first part. If user tries to navigate to next page which is not
         * in the result list, we need to get these items. 显示的可选择的总数
         **/
        public int mTotalChoicesNum;

        /**
         * Candidate list. The first one is the full-sentence candidate. 候选词列表
         */
        public List<String> mCandidatesList = new Vector<String>();

        /**
         * Element i stores the starting position of page i. 页的开始位置
         */
        public Vector<Integer> mPageStart = new Vector<Integer>();

        /**
         * Element i stores the number of characters to page i. 每一页的数量
         */
        public Vector<Integer> mCnToPage = new Vector<Integer>();

        /**
         * The position to delete in Pinyin string. If it is less than 0, IME
         * will do an incremental search, otherwise IME will do a deletion
         * operation. if {@link #mIsPosInSpl} is true, IME will delete the whole
         * string for mPosDelSpl-th spelling, otherwise it will only delete
         * mPosDelSpl-th character in the Pinyin string. 在拼音字符串中的删除位置
         */
        public int mPosDelSpl = -1;

        /**
         * If {@link #mPosDelSpl} is big than or equal to 0, this member is used
         * to indicate that whether the postion is counted in spelling id or
         * character. 如果 mPosDelSpl 大于等于 0，那么这个参数就用于表明是否是 拼写的id 或者 字符。
         */
        public boolean mIsPosInSpl;

        public DecodingInfo(Context context) {
            mSurface = new StringBuffer();
            mSurfaceDecodedLen = 0;
            mPinyinDecoderService = new PinyinDecoderService(context);

        }

        /**
         * 重置
         */
        public void reset() {
            mSurface.delete(0, mSurface.length());
            mSurfaceDecodedLen = 0;
            mCursorPos = 0;
            mFullSent = "";
            mFixedLen = 0;
            mFinishSelection = false;
            mComposingStr = "";
            mComposingStrDisplay = "";
            mActiveCmpsLen = 0;
            mActiveCmpsDisplayLen = 0;

            resetCandidates();
        }

        /**
         * 候选词列表是否为空
         *
         * @return
         */
        public boolean isCandidatesListEmpty() {
            return mCandidatesList.size() == 0;
        }

        /**
         * 拼写的字符串是否已满
         *
         * @return
         */
        public boolean isSplStrFull() {
            if (mSurface.length() >= PY_STRING_MAX - 1)
                return true;
            return false;
        }

        /**
         * 增加拼写字符
         *
         * @param ch
         * @param reset
         *            拼写字符是否重置
         */
        public void addSplChar(char ch, boolean reset) {
            if (reset) {
                mSurface.delete(0, mSurface.length());
                mSurfaceDecodedLen = 0;
                mCursorPos = 0;
                try {
                    mPinyinDecoderService.imResetSearch();
                } catch (Exception e) {
                }
            }
            mSurface.insert(mCursorPos, ch);
            mCursorPos++;
        }

        // Prepare to delete before cursor. We may delete a spelling char if
        // the cursor is in the range of unfixed part, delete a whole spelling
        // if the cursor in inside the range of the fixed part.
        // This function only marks the position used to delete.
        /**
         * 删除前的准备。该函数只是标记要删除的位置。
         */
        public void prepareDeleteBeforeCursor() {
            if (mCursorPos > 0) {
                int pos;

                for (pos = 0; pos < mFixedLen; pos++) {
                    if (mSplStart[pos + 2] >= mCursorPos
                            && mSplStart[pos + 1] < mCursorPos) {
                        // 删除一个拼写字符串
                        mPosDelSpl = pos;
                        mCursorPos = mSplStart[pos + 1];
                        mIsPosInSpl = true;
                        break;
                    }
                }

                if (mPosDelSpl < 0) {
                    // 删除一个字符
                    mPosDelSpl = mCursorPos - 1;
                    mCursorPos--;
                    mIsPosInSpl = false;
                }
            }
        }

        /**
         * 获取拼音字符串长度
         *
         * @return
         */
        public int length() {
            return mSurface.length();
        }

        /**
         * 获得拼音字符串中指定位置的字符
         *
         * @param index
         * @return
         */
        public char charAt(int index) {
            return mSurface.charAt(index);
        }

        /**
         * 获得拼音字符串
         *
         * @return
         */
        public StringBuffer getOrigianlSplStr() {
            return mSurface;
        }

        /**
         * 获得成功解码的字符串长度
         *
         * @return
         */
        public int getSplStrDecodedLen() {
            return mSurfaceDecodedLen;
        }

        /**
         * 获得每个拼写字符串的开始位置
         *
         * @return
         */
        public int[] getSplStart() {
            return mSplStart;
        }

        /**
         * 获取拼音字符串，有可能存在选中的候选词
         *
         * @return
         */
        public String getComposingStr() {
            return mComposingStr;
        }

        /**
         * 获取活动的拼音字符串，就是选择了的候选词。
         *
         * @return
         */
        public String getComposingStrActivePart() {
            assert (mActiveCmpsLen <= mComposingStr.length());
            return mComposingStr.substring(0, mActiveCmpsLen);
        }

        /**
         * 获得活动的拼音字符串长度
         *
         * @return
         */
        public int getActiveCmpsLen() {
            return mActiveCmpsLen;
        }

        /**
         * 获取显示的拼音字符串
         *
         * @return
         */
        public String getComposingStrForDisplay() {
            return mComposingStrDisplay;
        }

        /**
         * 显示的拼音字符串的长度
         *
         * @return
         */
        public int getActiveCmpsDisplayLen() {
            return mActiveCmpsDisplayLen;
        }

        /**
         * 第一个完整句子
         *
         * @return
         */
        public String getFullSent() {
            return mFullSent;
        }

        private static final String TAG = "CLOUD_INPUT";

        /**
         * 获取当前完整句子
         *
         * @param activeCandPos
         * @return
         */
        public String getCurrentFullSent(int activeCandPos) {
            try {
                String retStr = mFullSent.substring(0, mFixedLen);
                retStr += mCandidatesList.get(activeCandPos);
                return retStr;
            } catch (Exception e) {
                return "";
            }
        }

        /**
         * 重置候选词列表
         */
        public void resetCandidates() {
            mCandidatesList.clear();
            mTotalChoicesNum = 0;

            mPageStart.clear();
            mPageStart.add(0);
            mCnToPage.clear();
            mCnToPage.add(0);
        }

        /**
         * 候选词来自app，判断输入法状态 mImeState == ImeState.STATE_APP_COMPLETION。
         *
         * @return
         */
//        public boolean candidatesFromApp() {
//            return ImeState.STATE_APP_COMPLETION == mImeState;
//        }

        /**
         * 判断 mComposingStr.length() == mFixedLen ？
         *
         * @return
         */
        public boolean canDoPrediction() {
            return mComposingStr.length() == mFixedLen;
        }

        /**
         * 选择是否完成
         *
         * @return
         */
        public boolean selectionFinished() {
            return mFinishSelection;
        }

        // After the user chooses a candidate, input method will do a
        // re-decoding and give the new candidate list.
        // If candidate id is less than 0, means user is inputting Pinyin,
        // not selecting any choice.
        /**
         * 如果candId〉0，就选择一个候选词，并且重新获取一个候选词列表，选择的候选词存放在mComposingStr中，通过mDecInfo.
         * getComposingStrActivePart()取出来。如果candId小于0 ，就对输入的拼音进行查询。
         *
         * @param candId
         */
        private void chooseDecodingCandidate(int candId) {
          //  if (mImeState != ImeState.STATE_PREDICT) {
                resetCandidates();
                int totalChoicesNum = 0;
                try {
                    if (candId < 0) {
                        if (length() == 0) {
                            totalChoicesNum = 0;
                        } else {
                            if (mPyBuf == null)
                                mPyBuf = new byte[PY_STRING_MAX];
                            for (int i = 0; i < length(); i++)
                                mPyBuf[i] = (byte) charAt(i);
                            mPyBuf[length()] = 0;

                            if (mPosDelSpl < 0) {
                                totalChoicesNum = mPinyinDecoderService
                                        .imSearch(mPyBuf, length());
                            } else {
                                boolean clear_fixed_this_step = true;
//                                if (ImeState.STATE_COMPOSING == mImeState) {
//                                    clear_fixed_this_step = false;
//                                }
                                totalChoicesNum = mPinyinDecoderService
                                        .imDelSearch(mPosDelSpl, mIsPosInSpl,
                                                clear_fixed_this_step);
                                mPosDelSpl = -1;
                            }
                        }
                    } else {
                        totalChoicesNum = mPinyinDecoderService
                                .imChoose(candId);
                    }
                } catch (Exception e) {
                }
                updateDecInfoForSearch(totalChoicesNum);
       //     }
        }

        /**
         * 更新查询词库后的信息
         *
         * @param totalChoicesNum
         */
        private void updateDecInfoForSearch(int totalChoicesNum) {
            mTotalChoicesNum = totalChoicesNum;
            if (mTotalChoicesNum < 0) {
                mTotalChoicesNum = 0;
                return;
            }

            try {
                String pyStr;

                mSplStart = mPinyinDecoderService.imGetSplStart();
                pyStr = mPinyinDecoderService.imGetPyStr(false);
                mSurfaceDecodedLen = mPinyinDecoderService.imGetPyStrLen(true);
                assert (mSurfaceDecodedLen <= pyStr.length());

                mFullSent = mPinyinDecoderService.imGetChoice(0);
                mFixedLen = mPinyinDecoderService.imGetFixedLen();

                // Update the surface string to the one kept by engine.
                mSurface.replace(0, mSurface.length(), pyStr);

                if (mCursorPos > mSurface.length())
                    mCursorPos = mSurface.length();
                mComposingStr = mFullSent.substring(0, mFixedLen)
                        + mSurface.substring(mSplStart[mFixedLen + 1]);

                mActiveCmpsLen = mComposingStr.length();
                if (mSurfaceDecodedLen > 0) {
                    mActiveCmpsLen = mActiveCmpsLen
                            - (mSurface.length() - mSurfaceDecodedLen);
                }

                // Prepare the display string.
                if (0 == mSurfaceDecodedLen) {
                    mComposingStrDisplay = mComposingStr;
                    mActiveCmpsDisplayLen = mComposingStr.length();
                } else {
                    mComposingStrDisplay = mFullSent.substring(0, mFixedLen);
                    for (int pos = mFixedLen + 1; pos < mSplStart.length - 1; pos++) {
                        mComposingStrDisplay += mSurface.substring(
                                mSplStart[pos], mSplStart[pos + 1]);
                        if (mSplStart[pos + 1] < mSurfaceDecodedLen) {
                            mComposingStrDisplay += " ";
                        }
                    }
                    mActiveCmpsDisplayLen = mComposingStrDisplay.length();
                    if (mSurfaceDecodedLen < mSurface.length()) {
                        mComposingStrDisplay += mSurface
                                .substring(mSurfaceDecodedLen);
                    }
                }

                if (mSplStart.length == mFixedLen + 2) {
                    mFinishSelection = true;
                } else {
                    mFinishSelection = false;
                }
            }  catch (Exception e) {
                mTotalChoicesNum = 0;
                mComposingStr = "";
            }
            // Prepare page 0.
            if (!mFinishSelection) {
                preparePage(0);
            }
        }

        /**
         * 选择预报候选词
         *
         * @param choiceId
         */
        private void choosePredictChoice(int choiceId) {
//            if (ImeState.STATE_PREDICT != mImeState || choiceId < 0
//                    || choiceId >= mTotalChoicesNum) {
//                return;
//            }

            String tmp = mCandidatesList.get(choiceId);

            resetCandidates();

            mCandidatesList.add(tmp);
            mTotalChoicesNum = 1;

            mSurface.replace(0, mSurface.length(), "");
            mCursorPos = 0;
            mFullSent = tmp;
            mFixedLen = tmp.length();
            mComposingStr = mFullSent;
            mActiveCmpsLen = mFixedLen;

            mFinishSelection = true;
        }

        /**
         * 获得指定的候选词
         *
         * @param candId
         * @return
         */
        public String getCandidate(int candId) {
            // Only loaded items can be gotten, so we use mCandidatesList.size()
            // instead mTotalChoiceNum.
            if (candId < 0 || candId > mCandidatesList.size()) {
                return null;
            }
            return mCandidatesList.get(candId);
        }

        /**
         * 从缓存中获取一页的候选词，然后放进mCandidatesList中。三种不同的获取方式：1、mIPinyinDecoderService.
         * imGetChoiceList
         * （）；2、mIPinyinDecoderService.imGetPredictList；3、从mAppCompletions[]取。
         */
        private void getCandiagtesForCache() {
            int fetchStart = mCandidatesList.size();
            int fetchSize = mTotalChoicesNum - fetchStart;
            if (fetchSize > MAX_PAGE_SIZE_DISPLAY) {
                fetchSize = MAX_PAGE_SIZE_DISPLAY;
            }
            try {
                List<String> newList = null;
                newList = mPinyinDecoderService.imGetChoiceList(
                        fetchStart, fetchSize, mFixedLen);
//                if (ImeState.STATE_INPUT == mImeState
//                        || ImeState.STATE_IDLE == mImeState
//                        || ImeState.STATE_COMPOSING == mImeState) {
//                    newList = mPinyinDecoderService.imGetChoiceList(
//                            fetchStart, fetchSize, mFixedLen);
//                } else if (ImeState.STATE_PREDICT == mImeState) {
//                    newList = mPinyinDecoderService.imGetPredictList(
//                            fetchStart, fetchSize);
//                } else if (ImeState.STATE_APP_COMPLETION == mImeState) {
//                    newList = new ArrayList<String>();
//                    if (null != mAppCompletions) {
//                        for (int pos = fetchStart; pos < fetchSize; pos++) {
//                            CompletionInfo ci = mAppCompletions[pos];
//                            if (null != ci) {
//                                CharSequence s = ci.getText();
//                                if (null != s)
//                                    newList.add(s.toString());
//                            }
//                        }
//                    }
//                }
                mCandidatesList.addAll(newList);
            } catch (Exception e) {
                Log.w(TAG, "PinyinDecoderService died", e);
            }
        }

        /**
         * 判断指定页是否准备好了？
         *
         * @param pageNo
         * @return
         */
        public boolean pageReady(int pageNo) {
            // If the page number is less than 0, return false
            if (pageNo < 0)
                return false;

            // Page pageNo's ending information is not ready.
            if (mPageStart.size() <= pageNo + 1) {
                return false;
            }

            return true;
        }

        /**
         * 准备指定页，从缓存中取出指定页的候选词。
         *
         * @param pageNo
         * @return
         */
        public boolean preparePage(int pageNo) {
            // If the page number is less than 0, return false
            if (pageNo < 0)
                return false;

            // Make sure the starting information for page pageNo is ready.
            if (mPageStart.size() <= pageNo) {
                return false;
            }

            // Page pageNo's ending information is also ready.
            if (mPageStart.size() > pageNo + 1) {
                return true;
            }

            // If cached items is enough for page pageNo.
            if (mCandidatesList.size() - mPageStart.elementAt(pageNo) >= MAX_PAGE_SIZE_DISPLAY) {
                return true;
            }

            // Try to get more items from engine
            getCandiagtesForCache();

            // Try to find if there are available new items to display.
            // If no new item, return false;
            if (mPageStart.elementAt(pageNo) >= mCandidatesList.size()) {
                return false;
            }

            // If there are new items, return true;
            return true;
        }

        /**
         * 准备预报候选词
         *
         * @param history
         */
        public void preparePredicts(CharSequence history) {
            if (null == history)
                return;

            resetCandidates();

//            if (Settings.getPrediction()) {
                String preEdit = history.toString();
                int predictNum = 0;
                if (null != preEdit) {
                    try {
                        mTotalChoicesNum = mPinyinDecoderService
                                .imGetPredictsNum(preEdit);
                    } catch (Exception e) {
                        return;
                    }
                }
//            }

            preparePage(0);
            mFinishSelection = false;
        }

        /**
         * 准备从app获取候选词
         *
         * @param completions
         */
        private void prepareAppCompletions(CompletionInfo completions[]) {
            resetCandidates();
            mAppCompletions = completions;
            mTotalChoicesNum = completions.length;
            preparePage(0);
            mFinishSelection = false;
            return;
        }

        /**
         * 获取当前页的长度
         *
         * @param currentPage
         * @return
         */
        public int getCurrentPageSize(int currentPage) {
            if (mPageStart.size() <= currentPage + 1)
                return 0;
            return mPageStart.elementAt(currentPage + 1)
                    - mPageStart.elementAt(currentPage);
        }

        /**
         * 获取当前页的开始位置
         *
         * @param currentPage
         * @return
         */
        public int getCurrentPageStart(int currentPage) {
            if (mPageStart.size() < currentPage + 1)
                return mTotalChoicesNum;
            return mPageStart.elementAt(currentPage);
        }

        /**
         * 是否还有下一页？
         *
         * @param currentPage
         * @return
         */
        public boolean pageForwardable(int currentPage) {
            if (mPageStart.size() <= currentPage + 1)
                return false;
            if (mPageStart.elementAt(currentPage + 1) >= mTotalChoicesNum) {
                return false;
            }
            return true;
        }

        /**
         * 是否有上一页
         *
         * @param currentPage
         * @return
         */
        public boolean pageBackwardable(int currentPage) {
            if (currentPage > 0)
                return true;
            return false;
        }

        /**
         * 光标前面的字符是否是分隔符“'”
         *
         * @return
         */
        public boolean charBeforeCursorIsSeparator() {
            int len = mSurface.length();
            if (mCursorPos > len)
                return false;
            if (mCursorPos > 0 && mSurface.charAt(mCursorPos - 1) == '\'') {
                return true;
            }
            return false;
        }

        /**
         * 获取光标位置
         *
         * @return
         */
        public int getCursorPos() {
            return mCursorPos;
        }

        /**
         * 获取光标在拼音字符串中的位置
         *
         * @return
         */
        public int getCursorPosInCmps() {
            int cursorPos = mCursorPos;
            int fixedLen = 0;

            for (int hzPos = 0; hzPos < mFixedLen; hzPos++) {
                if (mCursorPos >= mSplStart[hzPos + 2]) {
                    cursorPos -= mSplStart[hzPos + 2] - mSplStart[hzPos + 1];
                    cursorPos += 1;
                }
            }
            return cursorPos;
        }

        /**
         * 获取光标在显示的拼音字符串中的位置
         *
         * @return
         */
        public int getCursorPosInCmpsDisplay() {
            int cursorPos = getCursorPosInCmps();
            // +2 is because: one for mSplStart[0], which is used for other
            // purpose(The length of the segmentation string), and another
            // for the first spelling which does not need a space before it.
            for (int pos = mFixedLen + 2; pos < mSplStart.length - 1; pos++) {
                if (mCursorPos <= mSplStart[pos]) {
                    break;
                } else {
                    cursorPos++;
                }
            }
            return cursorPos;
        }

        /**
         * 移动光标到末尾
         *
         * @param left
         */
        public void moveCursorToEdge(boolean left) {
            if (left)
                mCursorPos = 0;
            else
                mCursorPos = mSurface.length();
        }

        // Move cursor. If offset is 0, this function can be used to adjust
        // the cursor into the bounds of the string.
        /**
         * 移动光标
         *
         * @param offset
         */
        public void moveCursor(int offset) {
            if (offset > 1 || offset < -1)
                return;

            if (offset != 0) {
                int hzPos = 0;
                for (hzPos = 0; hzPos <= mFixedLen; hzPos++) {
                    if (mCursorPos == mSplStart[hzPos + 1]) {
                        if (offset < 0) {
                            if (hzPos > 0) {
                                offset = mSplStart[hzPos]
                                        - mSplStart[hzPos + 1];
                            }
                        } else {
                            if (hzPos < mFixedLen) {
                                offset = mSplStart[hzPos + 2]
                                        - mSplStart[hzPos + 1];
                            }
                        }
                        break;
                    }
                }
            }
            mCursorPos += offset;
            if (mCursorPos < 0) {
                mCursorPos = 0;
            } else if (mCursorPos > mSurface.length()) {
                mCursorPos = mSurface.length();
            }
        }

        /**
         * 获取拼写字符串的数量
         *
         * @return
         */
        public int getSplNum() {
            return mSplStart[0];
        }

        /**
         * 获取固定的字符的数量
         *
         * @return
         */
        public int getFixedLen() {
            return mFixedLen;
        }
    }
}
