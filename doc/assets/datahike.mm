<map version="freeplane 1.6.0">
<!--To view this file, download free mind mapping software Freeplane from http://freeplane.sourceforge.net -->
<node TEXT="datahike" FOLDED="false" ID="ID_901147632" CREATED="1570447225315" MODIFIED="1570448402457" STYLE="oval">
<font SIZE="18"/>
<hook NAME="MapStyle" zoom="3.064">
    <properties edgeColorConfiguration="#808080ff,#ff0000ff,#0000ffff,#00ff00ff,#ff00ffff,#00ffffff,#7c0000ff,#00007cff,#007c00ff,#7c007cff,#007c7cff,#7c7c00ff" fit_to_viewport="false"/>

<map_styles>
<stylenode LOCALIZED_TEXT="styles.root_node" STYLE="oval" UNIFORM_SHAPE="true" VGAP_QUANTITY="24.0 pt">
<font SIZE="24"/>
<stylenode LOCALIZED_TEXT="styles.predefined" POSITION="right" STYLE="bubble">
<stylenode LOCALIZED_TEXT="default" ICON_SIZE="12.0 pt" COLOR="#000000" STYLE="fork">
<font NAME="SansSerif" SIZE="10" BOLD="false" ITALIC="false"/>
</stylenode>
<stylenode LOCALIZED_TEXT="defaultstyle.details"/>
<stylenode LOCALIZED_TEXT="defaultstyle.attributes">
<font SIZE="9"/>
</stylenode>
<stylenode LOCALIZED_TEXT="defaultstyle.note" COLOR="#000000" BACKGROUND_COLOR="#ffffff" TEXT_ALIGN="LEFT"/>
<stylenode LOCALIZED_TEXT="defaultstyle.floating">
<edge STYLE="hide_edge"/>
<cloud COLOR="#f0f0f0" SHAPE="ROUND_RECT"/>
</stylenode>
</stylenode>
<stylenode LOCALIZED_TEXT="styles.user-defined" POSITION="right" STYLE="bubble">
<stylenode LOCALIZED_TEXT="styles.topic" COLOR="#18898b" STYLE="fork">
<font NAME="Liberation Sans" SIZE="10" BOLD="true"/>
</stylenode>
<stylenode LOCALIZED_TEXT="styles.subtopic" COLOR="#cc3300" STYLE="fork">
<font NAME="Liberation Sans" SIZE="10" BOLD="true"/>
</stylenode>
<stylenode LOCALIZED_TEXT="styles.subsubtopic" COLOR="#669900">
<font NAME="Liberation Sans" SIZE="10" BOLD="true"/>
</stylenode>
<stylenode LOCALIZED_TEXT="styles.important">
<icon BUILTIN="yes"/>
</stylenode>
</stylenode>
<stylenode LOCALIZED_TEXT="styles.AutomaticLayout" POSITION="right" STYLE="bubble">
<stylenode LOCALIZED_TEXT="AutomaticLayout.level.root" COLOR="#000000" STYLE="oval" SHAPE_HORIZONTAL_MARGIN="10.0 pt" SHAPE_VERTICAL_MARGIN="10.0 pt">
<font SIZE="18"/>
</stylenode>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,1" COLOR="#0033ff">
<font SIZE="16"/>
</stylenode>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,2" COLOR="#00b439">
<font SIZE="14"/>
</stylenode>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,3" COLOR="#990000">
<font SIZE="12"/>
</stylenode>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,4" COLOR="#111111">
<font SIZE="10"/>
</stylenode>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,5"/>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,6"/>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,7"/>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,8"/>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,9"/>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,10"/>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,11"/>
</stylenode>
</stylenode>
</map_styles>
</hook>
<hook NAME="AutomaticEdgeColor" COUNTER="50" RULE="ON_BRANCH_CREATION"/>
<node TEXT="bindings" POSITION="left" ID="ID_650263263" CREATED="1570448181120" MODIFIED="1570448346003">
<edge COLOR="#7c0000"/>
<node TEXT="Java" ID="ID_1194280915" CREATED="1570448331470" MODIFIED="1570448333118"/>
<node TEXT="Scala" ID="ID_865314601" CREATED="1570448335246" MODIFIED="1570448350263"/>
</node>
<node TEXT="data migration" POSITION="right" ID="ID_1579047982" CREATED="1570448081835" MODIFIED="1570448146367">
<edge COLOR="#7c007c"/>
<node TEXT="serialization" ID="ID_1929276762" CREATED="1570448226571" MODIFIED="1570448229333"/>
<node TEXT="what data?" ID="ID_1261809973" CREATED="1570448229720" MODIFIED="1570448233593">
<node TEXT="full eavt index" ID="ID_313432354" CREATED="1570456079609" MODIFIED="1570456091476"/>
<node TEXT="schema?" ID="ID_40298868" CREATED="1570456091907" MODIFIED="1570456095045">
<node TEXT="only for schema-on-write database" ID="ID_972963330" CREATED="1570459531857" MODIFIED="1570459540186"/>
<node TEXT="is as entity in data" ID="ID_1370983464" CREATED="1570459554430" MODIFIED="1570459562713"/>
</node>
<node TEXT="history?" ID="ID_1267152994" CREATED="1570456095462" MODIFIED="1570456109954">
<node TEXT="should be added with false in Datom" ID="ID_1610140801" CREATED="1570459523351" MODIFIED="1570463525298"/>
</node>
</node>
<node TEXT="what are supported sources and targets?" FOLDED="true" ID="ID_834637882" CREATED="1570454739664" MODIFIED="1570454756290">
<node TEXT="datomic" ID="ID_1704521498" CREATED="1570454762646" MODIFIED="1570454766325"/>
<node TEXT="crux" ID="ID_382877383" CREATED="1570454766651" MODIFIED="1570454767944"/>
<node TEXT="sql?" ID="ID_899136656" CREATED="1570454769232" MODIFIED="1570454772686"/>
</node>
<node TEXT="what about the meta entity?" ID="ID_1667088120" CREATED="1570459106761" MODIFIED="1570459112811">
<node TEXT="will be omitted because new meta entity added with new tx" ID="ID_1963077157" CREATED="1570459495437" MODIFIED="1570459508717"/>
</node>
<node TEXT="should this be a separate project?" ID="ID_877449031" CREATED="1570454788542" MODIFIED="1570454796480">
<node TEXT="yes!" ID="ID_1302421112" CREATED="1570454797794" MODIFIED="1570454799224"/>
</node>
</node>
<node TEXT="datalog" POSITION="left" ID="ID_1073990470" CREATED="1570448084434" MODIFIED="1570448360933" HGAP_QUANTITY="23.749999709427364 pt" VSHIFT_QUANTITY="-9.749999709427366 pt">
<edge COLOR="#007c7c"/>
<node TEXT="built-in functions" ID="ID_498507315" CREATED="1570448261401" MODIFIED="1570448276118"/>
<node TEXT="parser/compiler" ID="ID_411608567" CREATED="1570448248081" MODIFIED="1570448260998"/>
<node TEXT="query planner" ID="ID_1604907048" CREATED="1570448241376" MODIFIED="1570448247108"/>
</node>
<node TEXT="datascript legacy" POSITION="left" ID="ID_41042286" CREATED="1570448103390" MODIFIED="1570448123387">
<edge COLOR="#7c7c00"/>
</node>
<node TEXT="datomic compliancy" POSITION="left" ID="ID_1365593870" CREATED="1570448123775" MODIFIED="1570448128502">
<edge COLOR="#ff0000"/>
</node>
<node TEXT="identity and access management" POSITION="right" ID="ID_1489191066" CREATED="1570448364366" MODIFIED="1570448372718">
<edge COLOR="#007c7c"/>
</node>
<node TEXT="performance" POSITION="left" ID="ID_1454394533" CREATED="1570448404458" MODIFIED="1570448406588">
<edge COLOR="#ff0000"/>
</node>
<node TEXT="release process" POSITION="left" ID="ID_62699392" CREATED="1570448409571" MODIFIED="1570448415139">
<edge COLOR="#00ff00"/>
</node>
<node TEXT="remote interface" POSITION="right" ID="ID_679884679" CREATED="1570448190045" MODIFIED="1570448197232">
<edge COLOR="#00007c"/>
<node TEXT="HTTP server" ID="ID_721166001" CREATED="1570448198161" MODIFIED="1570448203022"/>
<node TEXT="client" ID="ID_562776218" CREATED="1570448203785" MODIFIED="1570448205002"/>
<node TEXT="cache" ID="ID_1215921217" CREATED="1570448205991" MODIFIED="1570448207402"/>
<node TEXT="serialization" ID="ID_129421294" CREATED="1570448208210" MODIFIED="1570448214753"/>
</node>
<node TEXT="modular design" POSITION="right" ID="ID_1476904129" CREATED="1570453125266" MODIFIED="1570454682219">
<edge COLOR="#ff00ff"/>
<node TEXT="how to do that?" ID="ID_710153645" CREATED="1570454684702" MODIFIED="1570454688533"/>
<node TEXT="what is the core?" ID="ID_737924866" CREATED="1570454689172" MODIFIED="1570454692357"/>
<node TEXT="how is the import structure?" ID="ID_1546191978" CREATED="1570454692817" MODIFIED="1570454699107"/>
<node TEXT="who maintains what?" ID="ID_102762859" CREATED="1570454699538" MODIFIED="1570454703741"/>
<node TEXT="under which domain?" ID="ID_273456706" CREATED="1570454840835" MODIFIED="1570454846026"/>
</node>
<node TEXT="schema" POSITION="left" ID="ID_50538689" CREATED="1570448136045" MODIFIED="1570453086637" HGAP_QUANTITY="17.749999888241295 pt" VSHIFT_QUANTITY="5.249999843537812 pt">
<edge COLOR="#00ff00"/>
<node TEXT="extensions via spec" ID="ID_204432921" CREATED="1570448291826" MODIFIED="1570453086636" HGAP_QUANTITY="15.499999955296518 pt" VSHIFT_QUANTITY="-15.749999530613437 pt"/>
<node TEXT="supported types" ID="ID_329749272" CREATED="1570448284696" MODIFIED="1570448288613">
<node TEXT="all datomic types?" ID="ID_192303384" CREATED="1570453020968" MODIFIED="1570453028980"/>
</node>
</node>
<node TEXT="tests" POSITION="left" ID="ID_423470020" CREATED="1570448407816" MODIFIED="1570448409198">
<edge COLOR="#0000ff"/>
<node TEXT="greater suite for time travel and schema" ID="ID_1116960513" CREATED="1570448421978" MODIFIED="1570453055275"/>
</node>
<node TEXT="time travel" POSITION="left" ID="ID_1061744624" CREATED="1570448128785" MODIFIED="1570448135537">
<edge COLOR="#0000ff"/>
<node TEXT="garbage collector" ID="ID_1725489762" CREATED="1570448313990" MODIFIED="1570448319017"/>
</node>
</node>
</map>
