package de.mide.pegsolitaire;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import static de.mide.pegsolitaire.model.PlaceStatusEnum.SPACE;
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
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.Space;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import de.mide.pegsolitaire.model.PlaceStatusEnum;
import de.mide.pegsolitaire.model.SpacePosition;

public class MainActivity extends AppCompatActivity implements OnClickListener {

    public static final String TAG4LOGGING = "PegSolitaire";

    /**
     * 棋盘名字。
     */
    /*private static final String[] _mapNames = {"English Style",
            "Easy",
            "French Style",
            "J. C. Wiegleb",
            "Asymmetrical 3-3-2-2",
            "Diamond"};*/
    private String[] _mapNames = null;

    /**
     * 用于存储棋盘初始化的数组。
     */
    private PlaceStatusEnum[][][] PLACE_INIT_ARRAY = null;

    /**
     *
     */
    private static final int _bestUsersNumber = 5;

    /**
     * 所有棋盘的长和宽。
     */
    private int[] _sizeOfColumns = null;

    private int[] _sizeOfRows = null;

    /**
     * 当前棋盘的长和宽。
     */
    private int _sizeColumn = -1;

    private int _sizeRow = -1;

    /**
     * 显示屏幕的宽。
     */
    private int _displayWidth = -1;

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
     * 是否存在已选中的棋子。
     */
    private boolean _selectedPegValid = false;
    /**
     * 选中的棋子的坐标。
     */
    private int _selectedPegColumn = -1;
    private int _selectedPegRow = -1;
    /**
     * 是否是同一个棋子。用于记录连跳。
     */
    private boolean _isSamePeg = false;
    /**
     * 当前棋盘样式编号。
     */
    private int _mapID = 0;

    /**
     * 用于存储棋盘上的棋子的按钮。
     */
    private ViewGroup.LayoutParams _buttonLayoutParams = null;

    /**
     * 棋盘上的棋子和空位置的布局。
     */
    private GridLayout _gridLayout = null;

    /**
     * 图片资源。
     */
    private Drawable _drawable_unselected_PEG = null;
    private Drawable _drawable_selected_PEG = null;
    private Drawable _drawable_space = null;
    private Drawable _drawable_ranklist_icon = null;
    private Drawable _drawable_success_icon = null;
    private Drawable _drawable_fail_icon = null;

    /**
     * 用于处理点击棋盘上的棋子的事件。
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i(TAG4LOGGING, "column=" + _sizeColumn + ", row=" + _sizeRow + "px:");

        _drawable_unselected_PEG = getDrawable(R.drawable.unselected_chess);
        _drawable_selected_PEG = getDrawable(R.drawable.selected_chess);
        _drawable_space = getDrawable(R.drawable.space);

        _drawable_ranklist_icon = getDrawable(R.drawable.ranklist_icon);
        _drawable_success_icon = getDrawable(R.drawable.success_icon);
        _drawable_fail_icon = getDrawable(R.drawable.fail_icon);

        _gridLayout = findViewById(R.id.boardGridLayout);

        retrieveData();
        displayResolutionEvaluate();
        actionBarConfiguration();
        initializeBoard(_mapID);
    }

    /**
     * 从 boards.json 里读取棋盘信息并将值写入适当的成员变量。
     */
    private void retrieveData() {

        try {

            BufferedReader br = new BufferedReader(new InputStreamReader(getAssets().open("boards.json"), StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();

            JSONArray boardsArray = new JSONObject(sb.toString()).getJSONArray("boards");

            int mapsSize = boardsArray.length();

            _mapNames = new String[mapsSize];
            _sizeOfColumns = new int[mapsSize];
            _sizeOfRows = new int[mapsSize];
            PLACE_INIT_ARRAY = new PlaceStatusEnum[mapsSize][][];

            for (int k = 0; k < mapsSize; k++) {

                _mapNames[k] = boardsArray.getJSONObject(k).getString("name");

                Log.i(TAG4LOGGING, "preparing map=" + _mapNames[k]);

                JSONArray JSONArray1 = boardsArray.getJSONObject(k).getJSONArray("map");
                _sizeOfColumns[k] = JSONArray1.length();
                PLACE_INIT_ARRAY[k] = new PlaceStatusEnum[_sizeOfColumns[k]][];

                for (int i = 0; i < _sizeOfColumns[k]; i++) {

                    JSONArray JSONArray2 = JSONArray1.getJSONArray(i);
                    _sizeOfRows[k] = JSONArray2.length();
                    PLACE_INIT_ARRAY[k][i] = new PlaceStatusEnum[_sizeOfRows[k]];

                    for (int j = 0; j < _sizeOfRows[k]; j++) {

                        PLACE_INIT_ARRAY[k][i][j] = getEnumFromInt((int)JSONArray2.get(j));
                    }

                }

            }


        } catch (IOException | JSONException e) {

            e.printStackTrace();
        }

    }

    /**
     * 从显示器读取分辨率并将值写入适当的成员变量。
     */
    private void displayResolutionEvaluate() {

        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);


        _displayWidth = displayMetrics.widthPixels;
        // 显示屏幕的高。
        int _displayHeight = displayMetrics.heightPixels;

        Log.i(TAG4LOGGING, "Display-Resolution: " + _displayWidth + "x" + _displayHeight);

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

        } else if (item.getItemId() == R.id.action_ranklist) {

            selectedRanklist();
            return true;

        } else if (item.getItemId() == R.id.action_maps) {

            selectedChangeMap();
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
                .setPositiveButton("是", (dialog, which) -> {
                    Log.i(TAG4LOGGING,"点击了是");
                    initializeBoard(_mapID);
                })
                .setNegativeButton("否", (dialog, which) -> Log.i(TAG4LOGGING,"点击了否"))
                .create()
                .show();
    }

    /**
     * 处理点击"名人堂"按钮的事件。
     * 弹出对话框，显示当前棋盘样式最高分及其姓名
     */
    public void selectedRanklist() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("名人堂")
                .setIcon(_drawable_ranklist_icon);

        SharedPreferences shp = getSharedPreferences("userdata", MODE_PRIVATE);

        String[] bestUsers = new String[_bestUsersNumber];
        int[] bestSteps = new int[_bestUsersNumber];

        for (int i = 0; i < _bestUsersNumber; i++) {
            bestUsers[i] = shp.getString(_mapID + "bestUser" + i, null);
            bestSteps[i] = shp.getInt(_mapID + "bestSteps" + i, -1);
        }

        if (bestUsers[0] == null) {
            builder.setMessage("当前棋盘样式还未有人通关！\n快来成为第一个吧！");
        } else {
            String showMessage = "当前棋盘样式：" + _mapNames[_mapID] + "\n";

            for (int i = 0; i < _bestUsersNumber; i++) {
                if (bestUsers[i] != null) {

                    showMessage += "第 " + (i + 1) + " 名：" + bestUsers[i] + "，步数：" + bestSteps[i] + " 步\n";
                } else {

                    showMessage += "第 " + (i + 1) + " 名：暂无\n";
                }

            }
            builder.setMessage(showMessage);
        }

        builder.setPositiveButton("关闭", (dialog, which) -> dialog.cancel())
                .create()
                .show();
    }

    /**
     * 处理点击"更换棋盘样式"按钮的事件。
     * 弹出对话框，询问用户需要更换哪一种棋盘样式。
     */
    public void selectedChangeMap() {

//        _mapID = (_mapID + 1) % PLACE_INIT_ARRAY.length;
        final int[] selectID = { _mapID };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("更换棋盘样式")
//                .setMessage("请在下列棋盘样式中选择一个：")
                .setSingleChoiceItems(_mapNames, selectID[0], (dialog, which) -> selectID[0] = which)
                .setPositiveButton("确定", (dialog, which) -> {
                    _mapID = selectID[0];
                    initializeBoard(_mapID);
                })
                .setNegativeButton("关闭", (dialog, which) -> dialog.cancel())
                .create()
                .show();

    }

    /**
     * 初始化棋盘上的棋子和空位置。
     */
    private void initializeBoard(int currentMapID) {

        _sizeColumn = _sizeOfColumns[currentMapID];
        _sizeRow = _sizeOfRows[currentMapID];

        int _sideLengthPlace = _displayWidth / _sizeColumn;

        _buttonLayoutParams = new ViewGroup.LayoutParams(_sideLengthPlace,
                _sideLengthPlace);

        _gridLayout.removeAllViews();
        _gridLayout.setColumnCount(_sizeRow);

        _numberOfSteps = 0;
        _numberOfPegs = 0;
        _selectedPegColumn = -1;
        _selectedPegRow = -1;
        _selectedPegValid = false;
        _isSamePeg = false;
        _mapID = currentMapID;
        _placeArray = new PlaceStatusEnum[_sizeColumn][_sizeRow];

        for (int i = 0; i < _sizeColumn; i++) {

            for (int j = 0; j < _sizeRow; j++) {

                PlaceStatusEnum placeStatus = PLACE_INIT_ARRAY[currentMapID][i][j];

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

        ImageButton button = new ImageButton(this);

        button.setLayoutParams(_buttonLayoutParams);
        button.setOnClickListener(this);

        SpacePosition pos = new SpacePosition(indexColumn, indexRow);
        button.setTag(pos);

        // Finished
        if (isPeg) {
            button.setForeground(_drawable_unselected_PEG);
            button.setBackground(_drawable_unselected_PEG);
            _numberOfPegs++;
        } else {
            button.setForeground(_drawable_space);
            button.setBackground(_drawable_space);
        }
        _gridLayout.addView(button);
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

        ImageButton clickedButton = (ImageButton)view;

        SpacePosition targetPosition = (SpacePosition)clickedButton.getTag();

        // 获取被点击的按钮的位置
        int indexColumn = targetPosition.getIndexColumn();
        int indexRow = targetPosition.getIndexRow();
        PlaceStatusEnum placeStatus = _placeArray[indexColumn][indexRow];

        switch (placeStatus) {

            case PEG:
                // Finished
                if (_selectedPegValid) {
                    // 将自己反选
                    if (_selectedPegColumn == indexColumn && _selectedPegRow == indexRow) {

                        clickedButton.setForeground(_drawable_unselected_PEG);
                        clickedButton.setBackground(_drawable_unselected_PEG);

                        _selectedPegColumn = -1;
                        _selectedPegRow = -1;
                        _selectedPegValid = false;
                    } else {
                        ImageButton selectedButton = getButtonFromPosition(
                                new SpacePosition(_selectedPegColumn, _selectedPegRow));

                        selectedButton.setForeground(_drawable_unselected_PEG);
                        selectedButton.setBackground(_drawable_unselected_PEG);
                        clickedButton.setForeground(_drawable_selected_PEG);
                        clickedButton.setBackground(_drawable_selected_PEG);

                        _selectedPegColumn = indexColumn;
                        _selectedPegRow = indexRow;
                        _selectedPegValid = true;
                    }
                } else {

                    clickedButton.setForeground(_drawable_selected_PEG);
                    clickedButton.setBackground(_drawable_selected_PEG);

                    _selectedPegColumn = indexColumn;
                    _selectedPegRow = indexRow;
                    _selectedPegValid = true;
                }
                _isSamePeg = false;

                break;

            case SPACE:
                // Finished
                if (_selectedPegValid) {

                    SpacePosition startPosition = new SpacePosition(_selectedPegColumn, _selectedPegRow);
                    SpacePosition skippedPosition = getSkippedPosition(startPosition, targetPosition);

                    if (skippedPosition != null) {
                        // 进行跳棋操作
                        jumpToPosition(getButtonFromPosition(startPosition),
                                getButtonFromPosition(targetPosition),
                                getButtonFromPosition(skippedPosition));
                        _isSamePeg = true;

                    } else {
                        // 提示当前操作不合法
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("提示")
                                .setMessage("当前操作不合法！")
                                .setPositiveButton("确认", (dialog, which) -> Log.i(TAG4LOGGING,"点击了确认"))
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
    private void jumpToPosition(ImageButton startButton, ImageButton targetButton, ImageButton skippedButton) {

        // Finished
        SpacePosition startPosition = (SpacePosition)startButton.getTag();
        SpacePosition targetPosition = (SpacePosition)targetButton.getTag();
        SpacePosition skippedPosition = (SpacePosition)skippedButton.getTag();

        // 开始棋子变成了空位
        startButton.setForeground(_drawable_space);
        startButton.setBackground(_drawable_space);
        _placeArray[startPosition.getIndexColumn()][startPosition.getIndexRow()] = SPACE;

        // 目标空位变成了一个棋子
        targetButton.setForeground(_drawable_selected_PEG);
        targetButton.setBackground(_drawable_selected_PEG);
        _placeArray[targetPosition.getIndexColumn()][targetPosition.getIndexRow()] = PEG;
        _selectedPegColumn = targetPosition.getIndexColumn();
        _selectedPegRow = targetPosition.getIndexRow();
        _selectedPegValid = true;

        // 被跳过的棋子变成了空位
        skippedButton.setForeground(_drawable_space);
        skippedButton.setBackground(_drawable_space);
        _placeArray[skippedPosition.getIndexColumn()][skippedPosition.getIndexRow()] = SPACE;

        _numberOfPegs--;
        // 如果并非连跳
        if (!_isSamePeg)
            _numberOfSteps++;
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
    private ImageButton getButtonFromPosition(SpacePosition position) {

        int index = position.getPlaceIndex(_sizeRow);

        return (ImageButton) _gridLayout.getChildAt(index);
    }

    /**
     * 返回值对应的枚举类型。
     * 如果没有，则返回 {@code null}。
     *
     * @param x Ordinal 值
     * @return 如果找到了对应的枚举类型，返回 {@code status}；
     * 否则返回 {@code null}
     */
    private PlaceStatusEnum getEnumFromInt(int x) {
        PlaceStatusEnum[] PlaceStatus = PlaceStatusEnum.values();
        for (PlaceStatusEnum status: PlaceStatus) {
            if (status.ordinal() == x)
                return status;
        }
        return null;
    }

    /**
     * 显示一个对话框，表明游戏已经胜利（只剩下一个棋子）。
     * 点击对话框上的按钮，可以重新开始游戏。
     * 在扩展版本中，你需要在这里添加一个输入框，让用户输入他的名字。
     */
    private void showVictoryDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("胜利")
                .setIcon(_drawable_success_icon);

        SharedPreferences shp = getSharedPreferences("userdata", MODE_PRIVATE);
        SharedPreferences.Editor editor = shp.edit();

        String[] bestUsers = new String[_bestUsersNumber];
        int[] bestSteps = new int[_bestUsersNumber];

        for (int i = 0; i < _bestUsersNumber; i++) {
            bestUsers[i] = shp.getString(_mapID + "bestUser" + i, null);
            bestSteps[i] = shp.getInt(_mapID + "bestSteps" + i, -1);
        }

        if (bestUsers[_bestUsersNumber - 1] == null || _numberOfSteps < bestSteps[_bestUsersNumber - 1]) {
            dialogBuilder.setMessage("您进入了最少步数名人堂前" + _bestUsersNumber + "名！\n请留下大名：");

            View view = View.inflate(MainActivity.this, R.layout.dialog_edittext, null);
            EditText userNameInput = view.findViewById(R.id.item_ed);
            dialogBuilder.setView(view);

            dialogBuilder.setPositiveButton("确定", (dialogInterface, which) -> {

                String currentUsername = userNameInput.getText().toString();

                int plugInPosition = -1;
                for (int i = 0; i < _bestUsersNumber; i++) {
                    if (bestUsers[i] == null || _numberOfSteps < bestSteps[i]) {
                        plugInPosition = i;
                        break;
                    }
                }

                for (int i = plugInPosition + 1; i + 1 < _bestUsersNumber; i++) {
                    bestUsers[i + 1] = bestUsers[i];
                    bestSteps[i + 1] = bestSteps[i];
                }

                bestUsers[plugInPosition] = currentUsername;
                bestSteps[plugInPosition] = _numberOfSteps;

                for (int i = 0; i < _bestUsersNumber; i++) {
                    editor.putString(_mapID + "bestUser" + i, bestUsers[i]);
                    editor.apply();
                    editor.putInt(_mapID + "bestSteps" + i, bestSteps[i]);
                    editor.apply();
                }

                Log.i(TAG4LOGGING, "mapID=" + _mapID);
                Log.i(TAG4LOGGING, "bestUser=" + currentUsername);
                Log.i(TAG4LOGGING, "bestSteps=" + _numberOfSteps);

                // 重新开始游戏
                initializeBoard(_mapID);
            });
        } else {
            dialogBuilder.setMessage("你赢了！");
            dialogBuilder.setPositiveButton("再来一局", (dialogInterface, i) -> {
                // 重新开始游戏
                initializeBoard(_mapID);
            });
        }

        dialogBuilder.create()
                .show();
    }

    /**
     * 显示一个对话框，表明游戏已经失败（没有可移动的棋子）。
     * 点击对话框上的按钮，可以重新开始游戏。
     */
    private void showFailureDialog(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("失败")
                .setMessage("你输了！")
                .setIcon(_drawable_fail_icon)
                .setPositiveButton("再来一局", (dialogInterface, i) -> {
            // 重新开始游戏
            initializeBoard(_mapID);
        })
                .create()
                .show();
    }

    /**
     * 给定一个起始位置和目标位置。
     * 如果移动合法，返回被跳过的位置。
     * 如果移动不合法，返回 {@code null}。
     * 移动合法的定义，参见作业文档。
     *
     * @param startPos  起始位置
     * @param targetPos 目标位置
     * @return 移动合法时，返回一个新 {@code SpacePosition}
     * 表示被跳过的位置；否则返回 {@code null}
     */
    private SpacePosition getSkippedPosition(SpacePosition startPos, SpacePosition targetPos) {
        // Finished
        int x1 = startPos.getIndexColumn(), y1 = startPos.getIndexRow();
        int x2 = targetPos.getIndexColumn(), y2 = targetPos.getIndexRow();
        // 保证起始位置和目标位置隔两格
        if ((x1 == x2 && (y1 == y2 - 2) || (y1 == y2 + 2))
                || (y1 == y2 && (x1 == x2 - 2) || (x1 == x2 + 2))
                && x1 >= 0 && x1 < _sizeColumn && y1 >= 0 && y1 < _sizeRow
                && x2 >= 0 && x2 < _sizeColumn && y2 >= 0 && y2 < _sizeRow) {

            int x3 = (x1 + x2) / 2, y3 = (y1 + y2) / 2;
            // 起始位置上需要有棋子，目标位置上应该为空；二者中点应该也需要有棋子，即为所求
            if (_placeArray[x1][y1] == PEG && _placeArray[x2][y2] == SPACE && _placeArray[x3][y3] == PEG)
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
                    // 比较上下左右四个方向，最近的四个目标格子
                    if (i - 2 >= 0 &&
                            getSkippedPosition(new SpacePosition(i, j), new SpacePosition(i - 2, j)) != null)
                        return true;
                    if (i + 2 < _sizeColumn &&
                            getSkippedPosition(new SpacePosition(i, j), new SpacePosition(i + 2, j)) != null)
                        return true;
                    if (j - 2 >= 0 &&
                            getSkippedPosition(new SpacePosition(i, j), new SpacePosition(i, j - 2)) != null)
                        return true;
                    if (j + 2 < _sizeRow &&
                            getSkippedPosition(new SpacePosition(i, j), new SpacePosition(i, j + 2)) != null)
                        return true;
                }
            }
        }
        return false;
    }
}
