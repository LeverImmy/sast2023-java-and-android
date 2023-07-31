package de.mide.pegsolitaire;

import static java.lang.Math.min;
import static java.lang.Math.max;

import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import static de.mide.pegsolitaire.model.PlaceStatusEnum.SPACE;
import static de.mide.pegsolitaire.model.PlaceStatusEnum.BLOCKED;
import static de.mide.pegsolitaire.model.PlaceStatusEnum.PEG;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Space;
import android.widget.Toast;

import de.mide.pegsolitaire.model.PlaceStatusEnum;
import de.mide.pegsolitaire.model.SpacePosition;

public class MainActivity extends AppCompatActivity implements OnClickListener {

    public static final String TAG4LOGGING = "PegSolitaire";

    private static final int TEXT_COLOR_BROWN = 0xffa52a2a;
    private static final int TEXT_COLOR_RED = 0xffff0000;

    /**
     * Unicode字符：实心方块
     */
    private static final String TOKEN_MARK = "■";

    /**
     * 用于存储棋盘初始化的数组。
     */
    private static final PlaceStatusEnum[][] PLACE_INIT_ARRAY =
            {
                    {BLOCKED, BLOCKED, PEG, PEG, PEG, BLOCKED, BLOCKED},
                    {BLOCKED, BLOCKED, PEG, PEG, PEG, BLOCKED, BLOCKED},
                    {PEG, PEG, PEG, PEG, PEG, PEG, PEG},
                    {PEG, PEG, PEG, SPACE, PEG, PEG, PEG},
                    {PEG, PEG, PEG, PEG, PEG, PEG, PEG},
                    {BLOCKED, BLOCKED, PEG, PEG, PEG, BLOCKED, BLOCKED},
                    {BLOCKED, BLOCKED, PEG, PEG, PEG, BLOCKED, BLOCKED}
            };

    private final int _sizeColumn = PLACE_INIT_ARRAY.length;

    private final int _sizeRow = PLACE_INIT_ARRAY[0].length;

    /**
     * 用于存储棋盘上的棋子和空位置的数组。
     */
    private PlaceStatusEnum[][] _placeArray = null;

    /**
     * 当前棋盘上的棋子数量。
     */
    private int _numberOfPegs = -1;
    /**
     * 当前执行的步数。
     */
    private int _numberOfSteps = -1;
    /**
     * 选中的棋子是否已经被移动了。
     */
    private boolean _selectedPegMoved = false;
    /**
     * 是否存在已选中的棋子。
     */
    private boolean _selectedPegValid = false;
    /**
     * 选中的棋子的坐标。
     */
    private int _selectedPegColumn = -1;
    private int _selectedPegRow = -1;

    /**
     * 用于存储棋盘上的棋子的按钮。
     */
    private ViewGroup.LayoutParams _buttonLayoutParams = null;

    /**
     * 用于开始新游戏的按钮。
     */
    private Button _startButton = null;

    /**
     * 棋盘上的棋子和空位置的布局。
     */
    private GridLayout _gridLayout = null;


    /**
     * 用于处理点击棋盘上的棋子的事件。
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i(TAG4LOGGING, "column=" + _sizeColumn + ", row=" + _sizeRow + "px:");

        _gridLayout = findViewById(R.id.boardGridLayout);

        displayResolutionEvaluate();
        actionBarConfiguration();
        initializeBoard();
    }

    /**
     * 从显示器读取分辨率并将值写入适当的成员变量。
     */
    private void displayResolutionEvaluate() {

        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);

        int displayWidth = displayMetrics.widthPixels;
        int displayHeight = displayMetrics.heightPixels;

        Log.i(TAG4LOGGING, "Display-Resolution: " + displayWidth + "x" + displayHeight);

        int _sideLengthPlace = displayWidth / _sizeColumn;

        _buttonLayoutParams = new ViewGroup.LayoutParams(_sideLengthPlace,
                _sideLengthPlace);
    }

    /**
     * 初始化操作栏。
     */
    private void actionBarConfiguration() {

        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) {

            Toast.makeText(this, "没有操作栏", Toast.LENGTH_LONG).show();
            return;
        }

        actionBar.setTitle("单人跳棋");
    }

    /**
     * 从资源文件加载操作栏菜单项。
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_menu_items, menu);

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 处理操作栏菜单项的选择。
     * 在扩展的版本中，你需要加入更多的菜单项。
     *
     * @param item 选择的菜单项
     * @return true: 选择的菜单项被处理了
     * false: 选择的菜单项没有被处理
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_new_game) {

            selectedNewGame();
            return true;

        } else
            return super.onOptionsItemSelected(item);
    }

    /**
     * 处理点击"新游戏"按钮的事件。
     * 弹出对话框，询问用户是否要开始新游戏。
     * 如果用户选择"是"，则初始化棋盘，否则不做任何事情。
     */
    public void selectedNewGame() {
        // Finished
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("新游戏")
                .setMessage("是否要开始新游戏？")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        initializeBoard();
                        Log.e(TAG4LOGGING,"点击了是");
                    }
                })
                .setNegativeButton("否",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.e(TAG4LOGGING,"点击了否");
                    }
                })
                .create()
                .show();
    }


    /**
     * 初始化棋盘上的棋子和空位置。
     */
    private void initializeBoard() {

        if (_gridLayout.getRowCount() == 0) {

            _gridLayout.setColumnCount(_sizeRow);

        } else { // 清除旧的棋盘

            _gridLayout.removeAllViews();
        }

        _numberOfSteps = 0;
        _numberOfPegs = 0;
        _selectedPegMoved = false;
        _selectedPegColumn = -1;
        _selectedPegRow = -1;
        _placeArray = new PlaceStatusEnum[_sizeColumn][_sizeRow];

        for (int i = 0; i < _sizeColumn; i++) {

            for (int j = 0; j < _sizeRow; j++) {

                PlaceStatusEnum placeStatus = PLACE_INIT_ARRAY[i][j];

                _placeArray[i][j] = placeStatus;

                switch (placeStatus) {

                    case PEG:
                        generateButton(i, j, true);
                        break;

                    case SPACE:
                        generateButton(i, j, false);
                        break;

                    case BLOCKED:
                        Space space = new Space(this); // Dummy-Element
                        _gridLayout.addView(space);
                        break;

                    default:
                        Log.e(TAG4LOGGING, "错误的棋盘状态");

                }
            }
        }

        Log.i(TAG4LOGGING, "棋盘初始化完成");
        updateDisplayStepsNumber();
    }

    /**
     * 生成棋盘上的一个位置。
     * 在基础任务中，棋盘上的棋子直接用字符 TOKEN_MARK 表示。
     * 在扩展任务中，棋盘上的棋子用图片表示。
     */
    private void generateButton(int indexColumn, int indexRow, boolean isPeg) {

        Button button = new Button(this);

        button.setTextSize(22.0f);
        button.setLayoutParams(_buttonLayoutParams);
        button.setOnClickListener(this);
        button.setTextColor(TEXT_COLOR_BROWN);

        SpacePosition pos = new SpacePosition(indexColumn, indexRow);
        button.setTag(pos);

        // TODO
        if (isPeg) {
            button.setText(TOKEN_MARK);
        } else {
            button.setText(" ");
        }
    }


    /**
     * 更新操作栏中的步数显示。
     */
    private void updateDisplayStepsNumber() {

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setSubtitle("执行步数：" + _numberOfSteps);
        }
    }

    /**
     * 处理棋盘上的点击事件。
     * 如果被点击的按钮是一个棋子，那么它将被改变选中状态。
     * 也就是说，如果它之前没有被选中，这个棋子会变为红色，
     * 同时，此前被选中的棋子（如果有）将变为棕色。
     * 或者如果它已经被选中，那么它自己将变为棕色。
     * 如果被点击的按钮是一个空位置，那么试图将被选中的棋子移动到该位置。
     * 如果移动成功，你需要更新棋盘上的棋子和空位置。
     * 如果移动失败，你需要显示一个错误信息。
     *
     * @param view 被点击的按钮
     *

     */
    @Override
    public void onClick(View view) {

        Button clickedButton = (Button) view;

        SpacePosition targetPosition = (SpacePosition) clickedButton.getTag();

        // 获取被点击的按钮的位置
        int indexColumn = targetPosition.getIndexColumn();
        int indexRow = targetPosition.getIndexRow();
        PlaceStatusEnum placeStatus = _placeArray[indexColumn][indexRow];

        switch (placeStatus) {

            case PEG:
                // Finished
                if (clickedButton.getCurrentTextColor() == TEXT_COLOR_BROWN) {
                    // 反选已选中的棋子（如果有）
                    if (_selectedPegValid) {
                        Button selectedButton = getButtonFromPosition(
                                new SpacePosition(_selectedPegColumn, _selectedPegRow));
                        selectedButton.setTextColor(TEXT_COLOR_BROWN);
                    }
                    // 再选中当前棋子
                    clickedButton.setTextColor(TEXT_COLOR_RED);
                    _selectedPegColumn = indexColumn;
                    _selectedPegRow = indexRow;
                    _selectedPegValid = true;
                } else if (clickedButton.getCurrentTextColor() == TEXT_COLOR_RED) {
                    clickedButton.setTextColor(TEXT_COLOR_BROWN);
                    _selectedPegColumn = -1;
                    _selectedPegRow = -1;
                    _selectedPegValid = false;
                }
                break;

            case SPACE:
                // Finished
                if (_selectedPegValid) {
                    SpacePosition startPosition = new SpacePosition(_selectedPegColumn, _selectedPegRow);
                    SpacePosition skippedPosition = getSkippedPosition(startPosition, targetPosition);
                    if (skippedPosition != null) {
                        jumpToPosition(getButtonFromPosition(startPosition),
                                getButtonFromPosition(targetPosition),
                                getButtonFromPosition(skippedPosition));
                        _selectedPegValid = false;
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("提示")
                                .setMessage("当前操作不合法！")
                                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Log.e(TAG4LOGGING,"点击了确认");
                                    }
                                })
                                .create()
                                .show();
                    }
                }

                break;

            default:
                Log.e(TAG4LOGGING, "错误的棋盘状态" + placeStatus);
        }
    }

    /**
     * 执行跳跃。仅当确定移动合法时才可以调用该方法。
     * 数组中三个位置的状态，和总棋子数发生变化。
     * 同时，在移动后，你需要检查是否已经结束游戏。
     *
     * @param startButton 被选中的棋子
     * @param targetButton 被选中的空位置
     * @param skippedButton 被跳过的棋子
     *
     */
    private void jumpToPosition(Button startButton, Button targetButton, Button skippedButton) {

        // Finished
        SpacePosition startPosition = (SpacePosition)startButton.getTag();
        SpacePosition targetPosition = (SpacePosition)targetButton.getTag();
        SpacePosition skippedPosition = (SpacePosition)skippedButton.getTag();

        startButton.setText(" ");
        _placeArray[startPosition.getIndexColumn()][startPosition.getIndexRow()] = SPACE;

        targetButton.setText(TOKEN_MARK);
        _placeArray[targetPosition.getIndexColumn()][targetPosition.getIndexRow()] = PEG;

        skippedButton.setText(" ");
        _placeArray[skippedPosition.getIndexColumn()][skippedPosition.getIndexRow()] = SPACE;

        _numberOfPegs--;
        updateDisplayStepsNumber();
        if (_numberOfPegs == 1) {
            showVictoryDialog();
        } else if (!has_movable_places()) {
            showFailureDialog();
        }
    }

    /**
     * 返回位置对应的按钮。
     *
     * @param position 位置
     * @return 按钮
     */
    private Button getButtonFromPosition(SpacePosition position) {

        int index = position.getPlaceIndex(_sizeRow);

        return (Button) _gridLayout.getChildAt(index);
    }

    /**
     * 显示一个对话框，表明游戏已经胜利（只剩下一个棋子）。
     * 点击对话框上的按钮，可以重新开始游戏。
     * 在扩展版本中，你需要在这里添加一个输入框，让用户输入他的名字。
     */
    private void showVictoryDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("胜利");
        dialogBuilder.setMessage("你赢了！");
        dialogBuilder.setPositiveButton("再来一局", (dialogInterface, i) -> {
            initializeBoard();  // 重新开始游戏
        });

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }

    /**
     * 显示一个对话框，表明游戏已经失败（没有可移动的棋子）。
     * 点击对话框上的按钮，可以重新开始游戏。
     */
    private void showFailureDialog(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("失败");
        dialogBuilder.setMessage("你输了！");
        dialogBuilder.setPositiveButton("再来一局", (dialogInterface, i) -> {
            initializeBoard();  // 重新开始游戏
        });

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }

    /**
     * 给定一个起始位置和目标位置。
     * 如果移动合法，返回被跳过的位置。
     * 如果移动不合法，返回 {@code null}。
     * 移动合法的定义，参见作业文档。
     *
     * @param startPos  起始位置
     * @param targetPos 目标位置
     * @return 移动合法时，返回一个新{@code SpacePosition}
     * 表示被跳过的位置；否则返回 {@code null}
     */
    private SpacePosition getSkippedPosition(SpacePosition startPos, SpacePosition targetPos) {
        // Finished
        int x1 = startPos.getIndexColumn(), y1 = startPos.getIndexRow();
        int x2 = targetPos.getIndexColumn(), y2 = targetPos.getIndexRow();
        int pegTotalNumber = 0, x3 = -1, y3 = -1;
        // 起始位置上需要有棋子，目标位置上应该为空
        if (_placeArray[x1][y1] == PEG && _placeArray[x2][y2] == SPACE) {
            // 在同一列
            if (x1 == x2) {
                for (int j = min(y1, y2) + 1; j <= max(y1, y2) - 1; j++) {
                    if (_placeArray[x1][j] == PEG) {
                        pegTotalNumber++;
                        x3 = x1;
                        y3 = j;
                    }
                    if (_placeArray[x1][j] == BLOCKED)
                        return null;
                }
            }
            // 在同一行
            if (y1 == y2) {
                for (int i = min(x1, x2) + 1; i <= max(x1, x2) - 1; i++) {
                    if (_placeArray[i][y1] == PEG) {
                        pegTotalNumber++;
                        x3 = i;
                        y3 = y1;
                    }
                    if (_placeArray[i][y1] == BLOCKED)
                        return null;
                }
            }
            // 如果路径上只有一个棋子，那么可以进行操作
            if (pegTotalNumber == 1)
                return new SpacePosition(x3, y3);
        }
        return null;
    }

    /**
     * 返回是否还有可移动的位置。
     *
     * @return 如果还有可移动的位置，返回 {@code true}
     * 否则返回 {@code false}
     */
    private Boolean has_movable_places(){
        for(int i = 0; i < _sizeColumn; i++){
            for(int j = 0; j < _sizeRow; j++){
                if(_placeArray[i][j] == PEG){
                    // Finished
                    for (int k = 0; k < _sizeColumn; k++)
                        if (getSkippedPosition(new SpacePosition(i, j), new SpacePosition(k, j)) != null)
                            return true;
                    for (int k = 0; k < _sizeRow; k++)
                        if (getSkippedPosition(new SpacePosition(i, j), new SpacePosition(i, k)) != null)
                            return true;
                }
            }
        }
        return false;
    }
}
