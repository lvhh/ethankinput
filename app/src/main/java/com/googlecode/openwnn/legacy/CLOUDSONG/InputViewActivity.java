package com.googlecode.openwnn.legacy.CLOUDSONG;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.inputmethod.pinyin.GooglePinyinIME;
import com.example.test.HandWritingBoardLayout;
import com.example.test.OnHandWritingRecognize;
import com.googlecode.openwnn.legacy.WnnWord;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.ethank.yungePad.R;

public class InputViewActivity extends ActionBarActivity implements OnCandidateSelected, OnHandWritingRecognize, OnPinyinQueryed {

    @Bind(R.id.keyboardGrid)
    GridLayout keyboardGrid;

    @Bind(R.id.handwrtingboard)
    HandWritingBoardLayout handWritingBoard;

    @Bind(R.id.candidateselected)
    TextView inputShow;

    @Bind(R.id.KEY_A)
    Button KEY_A;
    @Bind(R.id.KEY_B)
    Button KEY_B;
    @Bind(R.id.KEY_C)
    Button KEY_C;
    @Bind(R.id.KEY_D)
    Button KEY_D;
    @Bind(R.id.KEY_E)
    Button KEY_E;
    @Bind(R.id.KEY_F)
    Button KEY_F;
    @Bind(R.id.KEY_G)
    Button KEY_G;
    @Bind(R.id.KEY_H)
    Button KEY_H;
    @Bind(R.id.KEY_I)
    Button KEY_I;
    @Bind(R.id.KEY_J)
    Button KEY_J;
    @Bind(R.id.KEY_K)
    Button KEY_K;
    @Bind(R.id.KEY_L)
    Button KEY_L;
    @Bind(R.id.KEY_M)
    Button KEY_M;
    @Bind(R.id.KEY_N)
    Button KEY_N;
    @Bind(R.id.KEY_O)
    Button KEY_O;
    @Bind(R.id.KEY_P)
    Button KEY_P;
    @Bind(R.id.KEY_Q)
    Button KEY_Q;
    @Bind(R.id.KEY_R)
    Button KEY_R;
    @Bind(R.id.KEY_S)
    Button KEY_S;
    @Bind(R.id.KEY_T)
    Button KEY_T;
    @Bind(R.id.KEY_U)
    Button KEY_U;
    @Bind(R.id.KEY_V)
    Button KEY_V;
    @Bind(R.id.KEY_W)
    Button KEY_W;
    @Bind(R.id.KEY_X)
    Button KEY_X;
    @Bind(R.id.KEY_Y)
    Button KEY_Y;
    @Bind(R.id.KEY_Z)
    Button KEY_Z;
    @Bind(R.id.KEY_SPACE)
    Button KEY_SPACE;


    @Bind(R.id.container)
    LinearLayout container;


    @Bind(R.id.candidateContainer)
    RelativeLayout candidateContainer;

    @Bind(R.id.btn_showMore)
    Button mShowMore;

    @Bind(R.id.KEY_DEL)
    Button KEY_DEL;

    @Bind(R.id.selectkeyboard)
    Button btnSelectKeyboard;

    @Bind(R.id.selecthandwriting)
    Button btnSelectHandWriting;

    @Bind(R.id.clean)
    Button btnCleanHandWriting;

    @Bind(R.id.pinyinInput)
    TextView PinYinInput;


    CandidateView mCandidateView;

    private StringBuilder currentInput = new StringBuilder();

    private StringBuilder currentPinYin = new StringBuilder();

    private StringBuilder commitedText = new StringBuilder();

    private int currentIndex = 0;

    private CloudKeyboardInputManager ckManager;

    private GooglePinyinIME mGooglePinyinIME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_view);
        ButterKnife.bind(this);
        mGooglePinyinIME = new GooglePinyinIME(this);

        ckManager = new CloudKeyboardInputManager();
        ckManager.setOnPinyinQueryed(this);
        mCandidateView = new CandidateView(this);
        mCandidateView.setOnCandidateSelected(this);
        RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams
                (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp1.addRule(RelativeLayout.LEFT_OF, R.id.btn_showMore);

        lp1.width = ViewGroup.LayoutParams.MATCH_PARENT;
        candidateContainer.addView(mCandidateView, lp1);
        System.currentTimeMillis();
        keyboardGrid.setVisibility(View.VISIBLE);
        handWritingBoard.setVisibility(View.GONE);
        handWritingBoard.setOnHandWritingRecognize(this);
    }


    private void processInput(char[] chars) {
      //  ckManager.processInput(chars);
        mGooglePinyinIME.processChar(chars[0]);
    }


    private void clearCurrentInput() {
        currentInput.delete(0, currentInput.length());
    }

    @OnClick(R.id.KEY_SPACE)
    void inputTest() {
        clearCurrentInput();
    }

    @OnClick(R.id.KEY_A)
    void processInputA() {
        processInput(new char[]{'a'});
    }

    @OnClick(R.id.KEY_B)
    void processInputB() {
        processInput(new char[]{'b'});
    }

    @OnClick(R.id.KEY_C)
    void processInputC() {
        processInput(new char[]{'c'});
    }

    @OnClick(R.id.KEY_D)
    void processInputD() {
        processInput(new char[]{'d'});
    }

    @OnClick(R.id.KEY_E)
    void processInputE() {
        processInput(new char[]{'e'});
    }

    @OnClick(R.id.KEY_F)
    void processInputF() {
        processInput(new char[]{'f'});
    }

    @OnClick(R.id.KEY_G)
    void processInputG() {
        processInput(new char[]{'g'});
    }

    @OnClick(R.id.KEY_H)
    void processInputH() {
        processInput(new char[]{'h'});
    }

    @OnClick(R.id.KEY_I)
    void processInputI() {
        processInput(new char[]{'i'});
    }

    @OnClick(R.id.KEY_J)
    void processInputJ() {
        processInput(new char[]{'j'});
    }

    @OnClick(R.id.KEY_K)
    void processInputK() {
        processInput(new char[]{'k'});
    }

    @OnClick(R.id.KEY_L)
    void processInputL() {
        processInput(new char[]{'l'});
    }

    @OnClick(R.id.KEY_M)
    void processInputM() {
        processInput(new char[]{'m'});
    }

    @OnClick(R.id.KEY_N)
    void processInputN() {
        processInput(new char[]{'n'});
    }

    @OnClick(R.id.KEY_O)
    void processInputO() {
        processInput(new char[]{'o'});
    }

    @OnClick(R.id.KEY_P)
    void processInputP() {
        processInput(new char[]{'p'});
    }

    @OnClick(R.id.KEY_Q)
    void processInputQ() {
        processInput(new char[]{'q'});
    }

    @OnClick(R.id.KEY_R)
    void processInputR() {
        processInput(new char[]{'r'});
    }

    @OnClick(R.id.KEY_S)
    void processInputS() {
        processInput(new char[]{'s'});
    }

    @OnClick(R.id.KEY_T)
    void processInputT() {
        processInput(new char[]{'t'});
    }

    @OnClick(R.id.KEY_U)
    void processInputU() {
        processInput(new char[]{'u'});
    }

    @OnClick(R.id.KEY_V)
    void processInputV() {
        processInput(new char[]{'v'});
    }

    @OnClick(R.id.KEY_W)
    void processInputW() {
        processInput(new char[]{'w'});
    }

    @OnClick(R.id.KEY_X)
    void processInputX() {
        processInput(new char[]{'x'});
    }

    @OnClick(R.id.KEY_Y)
    void processInputY() {
        processInput(new char[]{'y'});
    }

    @OnClick(R.id.KEY_Z)
    void processInputZ() {
        processInput(new char[]{'z'});
    }

    @OnClick(R.id.KEY_SPACE)
    void processInputSpace() {
//        if (currentPinYin.length() == 0) {
//            commitedText.append(" ");
//            currentInput.append(" ");
//            currentIndex++;
//        } else {
//            currentInput.append(currentPinYin);
//            commitedText.append(currentPinYin);
//            currentIndex += currentPinYin.length();
//            for (int i = 0; i < currentPinYin.length(); ++i) {
//                mOpenWnnZHCN.deleteBy1();
//            }
//            cleanCurrentPinyinView();
//
//        }
//
//        inputShow.setText(currentInput.toString());
//        mCandidateView.clear();

    }

    @OnClick(R.id.KEY_DEL)
    void delCurrentInput() {
        if(isHandWriting()) {
            delCurrentInputNoPinyin();
        } else {
            if(PinYinInput.getText().length() > 0) {
                ckManager.processDel();

            } else {
                delCurrentInputNoPinyin();
            }
        }
    }

    private void delCurrentInputNoPinyin() {
        if(currentInput.length() > 0 ) {
            currentInput.deleteCharAt(currentInput.length() - 1);
        }
        inputShow.setText(currentInput.toString());
    }

    @OnClick(R.id.selecthandwriting)
    void showHandWriting() {
        handWritingBoard.setVisibility(View.VISIBLE);
        keyboardGrid.setVisibility(View.GONE);
        ckManager.delAll();
        PinYinInput.setText("");
        mCandidateView.clear();

    }

    @OnClick(R.id.selectkeyboard)
    void showKeyboard() {
        handWritingBoard.setVisibility(View.GONE);
        keyboardGrid.setVisibility(View.VISIBLE);
        resetHandWritingRecognize();
        mCandidateView.clear();
    }

    @OnClick(R.id.clean)
    void resetHandWritingRecognizeClicked() {
        resetHandWritingRecognize();
        mCandidateView.clear();
    }

    @Override
    public void candidateSelected(WnnWord wnnWord) {
        String candidate = null;
        if (wnnWord != null) {
            candidate = wnnWord.candidate;
        }
        if (!TextUtils.isEmpty(candidate)) {
            cleanCurrentPinyinView();
            appendCandidate(candidate);
            inputShow.setText(currentInput.toString());
        }
      //  mOpenWnnZHCN.commitTextSelected(wnnWord);
        mCandidateView.clear();
        if(isHandWriting()) {
            resetHandWritingRecognize();
        } else {
            ckManager.candidateSelected(wnnWord);
        }
    }

    private void cleanCurrentPinyinView() {
        PinYinInput.setText("");
    }

    private void appendCandidate(String candidate) {
        currentInput.append(candidate);
        currentIndex += candidate.length();
    }


    @Override
    public void handWritingRecognized(ArrayList<WnnWord> result) {
            mCandidateView.setSuggestions(result, false, false);
    }


    //TODO 整理一下

    private void resetHandWritingRecognize() {
        handWritingBoard.reset_recognize();
    }


    /*
      删除和上屏都要区分手写和字母输入；
     */
    private boolean isHandWriting() {
        return handWritingBoard.getVisibility() == View.VISIBLE;
    }


    @Override
    public void onPinyinQueryed(PinyinQueryResult pyQueryResult) {
        if(pyQueryResult!=null) {
            mCandidateView.setSuggestions(pyQueryResult.getCandidateList(), false, false);
            String pinyin = pyQueryResult.getCurrentInput();
                updatePinyin(pinyin);
        }
    }

    private void updatePinyin(String pinyin) {
        PinYinInput.setText(pinyin);
    }
}
