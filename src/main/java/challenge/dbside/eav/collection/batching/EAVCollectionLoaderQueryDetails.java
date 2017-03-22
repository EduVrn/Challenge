package challenge.dbside.eav.collection.batching;

import org.hibernate.eav.EAVGlobalContext;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.loader.plan.exec.internal.AbstractCollectionLoadQueryDetails;
import org.hibernate.loader.plan.exec.internal.AliasResolutionContextImpl;
import org.hibernate.loader.plan.exec.query.internal.SelectStatementBuilder;
import org.hibernate.loader.plan.exec.query.spi.QueryBuildingParameters;
import org.hibernate.loader.plan.spi.CollectionReturn;
import org.hibernate.loader.plan.spi.LoadPlan;
import org.hibernate.persister.entity.OuterJoinLoadable;
import org.hibernate.tuple.entity.EntityMetamodel;

import challenge.dbside.eav.EAVPersister;

import org.hibernate.tool.hbm2ddl.EAVDeployerImpl;

public class EAVCollectionLoaderQueryDetails extends AbstractCollectionLoadQueryDetails {

    private final String[] keyColumnNames;

    protected EAVCollectionLoaderQueryDetails(LoadPlan loadPlan, AliasResolutionContextImpl aliasResolutionContext,
            CollectionReturn rootReturn, QueryBuildingParameters buildingParameters,
            SessionFactoryImplementor factory) {
        super(loadPlan, aliasResolutionContext, modify(rootReturn), buildingParameters, factory);
        //this.keyColumnNames = ( (QueryableCollection) rootReturn.getCollectionPersister() ).getKeyColumnNames();
        this.keyColumnNames = new String[]{"entity_id1", "attribute_id"};

        generate();
    }

    @Override
    protected void applyRootReturnWhereJoinRestrictions(SelectStatementBuilder selectStatementBuilder) {
        //selectStatementBuilder.app

        selectStatementBuilder.appendRestrictions(getCollectionReferenceAliases().getCollectionTableAlias() + ".attribute_id = ?");
    }

    private static CollectionReturn modify(CollectionReturn rootReturn) {
        return rootReturn;
    }

    @Override
    protected String getRootTableAlias() {
        return getCollectionReferenceAliases().getCollectionTableAlias();
    }

    @Override
    protected void applyRootReturnSelectFragments(SelectStatementBuilder selectStatementBuilder) {
        String tableAlias = getCollectionReferenceAliases().getCollectionTableAlias();
        String colSuffix = getCollectionReferenceAliases().getCollectionColumnAliases().getSuffix();

        String selFragment = "/*RRSFColLoadQueryDet*/" + getQueryableCollection().selectFragment(
                tableAlias,
                colSuffix
        ) + "/*RRSFColLoadQueryDet*/";

        selectStatementBuilder.appendSelectClauseFragment(
                selFragment
        );
        if (getQueryableCollection().isManyToMany()) {
            final OuterJoinLoadable elementPersister = (OuterJoinLoadable) getQueryableCollection().getElementPersister();
            String elementAlias = getCollectionReferenceAliases().getElementTableAlias();
            String elemSuffix = getCollectionReferenceAliases().getEntityElementAliases().getColumnAliases().getSuffix();

            String manyToManySelFragment = "/*ColMTmSelFr*/" + elementPersister.selectFragment(
                    elementAlias,
                    elemSuffix
            ) + "/*ColMTmSelFr*/";

            selectStatementBuilder.appendSelectClauseFragment(
                    manyToManySelFragment
            );
        }
    }

    /*
	@Override
	protected void applyRootReturnTableFragments(SelectStatementBuilder selectStatementBuilder) {
		selectStatementBuilder.appendFromClauseFragment(
				getQueryableCollection().getTableName(),
				getCollectionReferenceAliases().getCollectionTableAlias()
		);
	}*/
    @Override
    protected void applyRootReturnTableFragments(SelectStatementBuilder selectStatementBuilder) {
        String tablename = getQueryableCollection().getTableName();
        String colAlias = getCollectionReferenceAliases().getCollectionTableAlias();

        String retFromTableFragment = "/*{RRTFColLoaderQueryDet}*/" + tablename + " "
                + colAlias + "\n";

        String acol = getQueryableCollection().getElementColumnNames()[0];

        String alias = getCollectionReferenceAliases().getElementTableAlias();
        String joinedAlias = "eav_join_";

        String baseSelectValues = "(select attribute_id, entity_id, text_value from "
                + EAVDeployerImpl.getTValuesName()
                + " where attribute_id = ?)";
        String joinings = "";

        EAVPersister e = (EAVPersister) getQueryableCollection().getElementPersister();
        EntityMetamodel mm = e.getEntityMetamodel();
        //EAVPersister e = (EAVPersister)this.get();
        int[] columnTableNumbers = e.getSubclassColumnTableNumberClosurePub();
        String[] columnReaderTemplates = e.getSubclassColumnReaderTemplateClosure();

        for (int i = 0; i < mm.getPropertySpan(); i++) {
            for (int k = 0; k < e.getPropertyColumnNames(i).length; k++) {
                String joining = "";

                String col = e.getPropertyColumnNames(i)[k].toLowerCase();
                if (col.equals("type_of_entity")) {
                    continue;
                }
                String table = joinedAlias + "tbl_" + col;
                //String table = alias / *+ "0_"* / + "tbl_" + col;
                //TODO HARDCODE
                if (col.toLowerCase().equals("type_of_entity")) {
                    continue;
                }

                String subalias = EAVPersister.generateTableAlias(alias,
                        /*placeHolderPart + name*/ columnReaderTemplates[k],
                        columnTableNumbers[k]).toLowerCase();

                joining += "inner join";
                //joining += "left outer join";
                col = col.replaceAll("_", "");
                Integer attrId = EAVGlobalContext.getTypeOfAttribute(col).getId();
                Integer lastParam = baseSelectValues.lastIndexOf("?");

                String selValuesAttr = new StringBuilder(baseSelectValues).replace(
                        lastParam, lastParam + 1, attrId.toString()).toString();

                joining += selValuesAttr;
                joining += " as " + table + "\n";
                joining += " 	on " + table + ".attribute_id = "
                        + attrId + "\n" + "		and "
                        + table + ".entity_id = " + colAlias + "." + acol + "\n";
                joinings += joining;
            }
        }

        retFromTableFragment += joinings;
        retFromTableFragment += "/*{RRTFColLoaderQueryDet}*/\n";

        selectStatementBuilder.appendFromClauseFragment(retFromTableFragment);
    }

    @Override
    protected void applyRootReturnOrderByFragments(SelectStatementBuilder selectStatementBuilder) {
        String tableAlias = getCollectionReferenceAliases().getElementTableAlias();

        final String manyToManyOrdering = getQueryableCollection().getManyToManyOrderByString(
                tableAlias
        );
        if (StringHelper.isNotEmpty(manyToManyOrdering)) {
            selectStatementBuilder.appendOrderByFragment(manyToManyOrdering);
        }
        //super.applyRootReturnOrderByFragments( selectStatementBuilder );
    }

}
