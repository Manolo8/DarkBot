package com.github.manolo8.darkbot.view;

import com.github.manolo8.darkbot.config.BoxInfo;
import com.github.manolo8.darkbot.config.CommonConfig;
import com.github.manolo8.darkbot.config.NpcInfo;
import com.github.manolo8.darkbot.core.manager.Core;
import com.github.manolo8.darkbot.core.manager.ModuleManager;
import com.github.manolo8.darkbot.core.manager.StarManager;
import com.github.manolo8.darkbot.core.utils.module.ModuleAndInfo;
import com.github.manolo8.darkbot.core.utils.module.ModuleConfig;
import com.github.manolo8.darkbot.view.builder.ConfigViewBuilder;
import com.github.manolo8.darkbot.view.builder.element.ElementBuilder;
import com.github.manolo8.darkbot.view.column.*;
import com.github.manolo8.darkbot.view.utils.MapGroup;
import com.github.manolo8.darkbot.view.utils.MapGroupCreator;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.util.List;
import java.util.stream.Collectors;

public class ConfigController {

    @FXML
    private ComboBox<String>  workingMap;
    @FXML
    private Slider            maxDeaths;
    @FXML
    private Slider            maxPetDeaths;
    @FXML
    private Slider            repairStart;
    @FXML
    private ComboBox<String>  respawn;
    @FXML
    private Slider            refreshMinutes;
    @FXML
    private ComboBox<String>  module;
    @FXML
    private Button            btnConfig;
    @FXML
    private ComboBox<Integer> offensiveConfig;
    @FXML
    private TextField         offensiveFormation;
    @FXML
    private ComboBox<Integer> defensiveConfig;
    @FXML
    private TextField         defensiveFormation;

    @FXML
    private TabPane tabPane;
    @FXML
    private Tab     paneModule;

    @FXML
    private TextField          boxesNameFilter;
    @FXML
    private TableView<BoxInfo> boxes;


    @FXML
    private ComboBox<MapGroup> npcMapFilter;
    @FXML
    private TextField          npcNameFilter;
    @FXML
    private TableView<NpcInfo> npcs;

    @FXML
    private ComboBox<String> selectedConfigModule;
    @FXML
    private Button           btnCurrentModule;
    @FXML
    private AnchorPane       panel;

    private ModuleManager      moduleManager;
    private StarManager        starManager;
    private CommonConfig       commonConfig;
    private Sync<CommonConfig> sync;

    void init(Core core) {

        ElementBuilder.init(core);

        moduleManager = core.getModuleManager();
        commonConfig = core.getCommonConfig();
        sync = new Sync<>(commonConfig);
        starManager = core.getStarManager();

        initGeneral();
        initBoxes();
        initNpcs();
        initModuleConfig();
    }

    private void initGeneral() {
        respawn.getItems().addAll("Base", "Portal", "Place");
        workingMap.getItems().addAll(starManager.getAllAvailableMaps());
        module.getItems().addAll(moduleManager.getModulesAndInfo().stream().filter(ModuleAndInfo::showInModules).map(ModuleAndInfo::getName).collect(Collectors.toList()));
        offensiveConfig.getItems().addAll(1, 2);
        defensiveConfig.getItems().addAll(1, 2);

        sync.controlString(workingMap, object -> starManager.fromId(object.WORKING_MAP).name, (object, value) -> object.WORKING_MAP = starManager.fromName(value).id);
        sync.controlIntegerByIndex(refreshMinutes, object -> object.REFRESH_TIME, (object, value) -> object.REFRESH_TIME = value);
        sync.controlIntegerByIndex(maxDeaths, object -> object.MAX_DEATHS, (object, value) -> object.MAX_DEATHS = value);
        sync.controlIntegerByIndex(maxPetDeaths, object -> object.MAX_PET_DEATHS, (object, value) -> object.MAX_PET_DEATHS = value);
        sync.controlIntegerByIndex(repairStart, object -> (int) (object.REPAIR_HP * 100), (object, value) -> object.REPAIR_HP = value / 100d);
        sync.controlIntegerByIndex(respawn, object -> object.RESPAWN, (object, value) -> object.RESPAWN = value);
        sync.controlInteger(offensiveConfig, object -> object.OFFENSIVE_CONFIG.configId, (object, value) -> object.OFFENSIVE_CONFIG.configId = value);
        sync.controlChar(offensiveFormation, object -> object.OFFENSIVE_CONFIG.formationKey, (object, value) -> object.OFFENSIVE_CONFIG.formationKey = value);
        sync.controlInteger(defensiveConfig, object -> object.RUN_CONFIG.configId, (object, value) -> object.RUN_CONFIG.configId = value);
        sync.controlChar(defensiveFormation, object -> object.RUN_CONFIG.formationKey, (object, value) -> object.RUN_CONFIG.formationKey = value);
        sync.controlString(module, object -> object.CURRENT_MODULE, this::moduleSet);

        btnConfig.setOnMouseClicked(event -> {
            selectedConfigModule.getSelectionModel().select(module.getSelectionModel().getSelectedItem());
            tabPane.getSelectionModel().select(paneModule);
        });
    }

    private void initNpcs() {

        ObservableList<NpcInfo> npcList = npcs.getItems();
        npcList.addAll(commonConfig.npcInfos.values());
        npcs.setItems(new FilteredList<>(npcList));
        commonConfig.addedNpc.subscribe(npcList::add);
        npcNameFilter.textProperty().addListener((observable, oldValue, newValue) -> this.npcsTableFilterChange((FilteredList<NpcInfo>) npcs.getItems()));
        npcMapFilter.getItems().addAll(createMapGroups());
        npcMapFilter.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> this.npcsTableFilterChange((FilteredList<NpcInfo>) npcs.getItems()));
        npcs.setEditable(true);
        npcs.setOnDragDone(event -> event.setDropCompleted(false));
        ObservableList<TableColumn<NpcInfo, ?>> npcColumns = npcs.getColumns();

        //noinspection unchecked
        npcColumns.addAll(
                new StringTableColumn<>("Name", 64, object -> object.name),
                new DoubleTableColumn<>("Radius", 64, object -> object.radius, (object, value) -> object.radius = value),
                new BooleanTableColumn<>("Circle", 64, object -> object.circle, (object, value) -> object.circle = value),
                new IntegerTableColumn<>("Priority", 64, object -> object.priority, (object, value) -> object.priority = value),
                new CharTableColumn<>("Ammo", 64, object -> object.ammo, (object, value) -> object.ammo = value),
                new BooleanTableColumn<>("Kill", 64, object -> object.kill, (object, value) -> object.kill = value),
                new BooleanTableColumn<>("Kamikaze", 64, object -> object.kamikaze, (object, value) -> object.kamikaze = value)
        );

        fixedWidth(npcs);

        npcMapFilter.getSelectionModel().select(0);
    }

    private List<MapGroup> createMapGroups() {

        MapGroupCreator creator = new MapGroupCreator(starManager);

        long commonFlag = MapGroup.SHOW_RADIUS | MapGroup.SHOW_CIRCLE | MapGroup.SHOW_PRIORITY | MapGroup.SHOW_AMMO | MapGroup.SHOW_KILL;
        long ggFlag     = MapGroup.SHOW_RADIUS | MapGroup.SHOW_PRIORITY | MapGroup.SHOW_AMMO | MapGroup.SHOW_USE_PET_KAMIKAZE;

        creator.addGroup("X-1", "[1-3]-1", commonFlag);
        creator.addGroup("X-2", "[1-3]-2", commonFlag);
        creator.addGroup("X-3", "[1-3]-3", commonFlag);
        creator.addGroup("X-4", "[1-3]-4", commonFlag);
        creator.addGroup("X-5", "[1-3]-5", commonFlag);
        creator.addGroup("X-6", "[1-3]-6", commonFlag);
        creator.addGroup("X-7", "[1-3]-7", commonFlag);
        creator.addGroup("X-8", "[1-3]-8", commonFlag);
        creator.addGroup("5-X", "5-[1-3]", commonFlag);
        creator.addGroup("4-5", "4-5", commonFlag);
        creator.addGroup("GG ALPHA", "GG ALPHA", ggFlag);
        creator.addGroup("GG BETA", "GG BETA", ggFlag);
        creator.addGroup("GG GAMMA", "GG GAMMA", ggFlag);

        return creator.getGroups();
    }

    private void initBoxes() {

        ObservableList<BoxInfo> boxList = boxes.getItems();
        boxList.addAll(commonConfig.boxInfos.values());
        boxes.setItems(new FilteredList<>(boxList));
        commonConfig.addedBox.subscribe(boxList::add);
        boxesNameFilter.textProperty().addListener((observable, oldValue, newValue) -> this.boxesTableFilterChange(newValue, (FilteredList<BoxInfo>) boxes.getItems()));
        boxes.setEditable(true);
        ObservableList<TableColumn<BoxInfo, ?>> boxColumns = boxes.getColumns();

        //noinspection unchecked
        boxColumns.addAll(
                new StringTableColumn<>("Name", 64, object -> object.name),
                new IntegerTableColumn<>("Wait", 64, object -> object.waitTime, (object, value) -> object.waitTime = value),
                new BooleanTableColumn<>("Collect", 64, object -> object.collect, (object, value) -> object.collect = value)
        );
        fixedWidth(boxes);
    }

    private void fixedWidth(TableView table) {
        table.widthProperty().addListener((observable, oldValue, newValue) -> fixWidth(table, newValue.intValue()));
    }

    private void fixWidth(TableView table, int width) {
        //noinspection unchecked
        ObservableList<TableColumn> columns = table.getColumns();
        int                         num     = columns.stream().mapToInt(value -> value.isVisible() ? 1 : 0).sum();

        for (int i = 0; i < columns.size(); i++) {
            TableColumn column = columns.get(i);

            if (i == 0)
                column.setPrefWidth(width - ((num - 1) * 64) - 15);
            else
                column.setPrefWidth(64);
        }
    }

    private void initModuleConfig() {

        List<ModuleAndInfo> infoList = moduleManager.getModulesAndInfo();

        selectedConfigModule.getSelectionModel().selectedItemProperty().addListener(this::moduleConfigChanged);

        selectedConfigModule.getItems()
                .addAll(infoList.stream()
                                .filter(info -> info.getConfig() != null)
                                .map(ModuleAndInfo::getName)
                                .collect(Collectors.toList())
                );

        btnCurrentModule.setOnMouseClicked(event -> {
            selectedConfigModule.getSelectionModel().select(module.getSelectionModel().getSelectedItem());
        });
    }

    private void npcsTableFilterChange(FilteredList<NpcInfo> filteredList) {

        String   search = npcNameFilter.getText();
        MapGroup group  = npcMapFilter.getValue();

        ObservableList<TableColumn<NpcInfo, ?>> columns = npcs.getColumns();
        for (int i = 0; i < columns.size(); i++) {
            TableColumn<NpcInfo, ?> npcInfoTableColumn = columns.get(i);
            npcInfoTableColumn.setVisible(group.hasColumn(i));
        }

        fixWidth(npcs, (int) npcs.getWidth());
        filteredList.setPredicate(npc -> (search.isEmpty() || npc.name.toLowerCase().contains(search)) && (group == null || group.contains(npc.mapList)));
    }

    private void boxesTableFilterChange(String newValue, FilteredList<BoxInfo> filteredList) {
        final String lower = newValue.toLowerCase();
        filteredList.setPredicate(box -> newValue.isEmpty() || box.name.toLowerCase().contains(lower));
    }

    private void moduleSet(CommonConfig object, String value) {
        moduleManager.setModuleByName(value);
        object.CURRENT_MODULE = value;
    }

    private void moduleConfigChanged(ObservableValue observable, String oldValue, String newValue) {

        ModuleAndInfo moduleAndInfo = moduleManager.getModuleAndInfo(newValue);

        panel.getChildren().clear();

        ModuleConfig config = moduleAndInfo.getConfig();

        ConfigViewBuilder builder = new ConfigViewBuilder(config);

        Node pane = builder.build();

        AnchorPane.setTopAnchor(pane, 0.0);
        AnchorPane.setBottomAnchor(pane, 0.0);
        AnchorPane.setLeftAnchor(pane, 0.0);
        AnchorPane.setRightAnchor(pane, 0.0);

        panel.getChildren().add(pane);
    }
}
