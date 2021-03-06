//package matrix.component;
//
//import java.lang.reflect.Array;
//import java.util.ArrayList;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.molgenis.data.Data;
//import org.molgenis.data.DecimalDataElement;
//import org.molgenis.data.TextDataElement;
//import org.molgenis.framework.db.Database;
//import org.molgenis.framework.db.DatabaseException;
//import org.molgenis.framework.db.Query;
//import org.molgenis.framework.db.QueryRule.Operator;
//import org.molgenis.matrix.MatrixException;
//import org.molgenis.matrix.component.AbstractObservationElementMatrix;
//import org.molgenis.matrix.component.general.MatrixColHeaderFilter;
//import org.molgenis.matrix.component.general.MatrixColValueFilter;
//import org.molgenis.matrix.component.general.MatrixQueryRule;
//import org.molgenis.matrix.component.general.MatrixRowHeaderFilter;
//import org.molgenis.matrix.component.general.MatrixRowValueFilter;
//import org.molgenis.matrix.component.interfaces.BasicMatrix;
//import org.molgenis.matrix.component.interfaces.SliceableMatrix;
//import org.molgenis.organization.Investigation;
//import org.molgenis.pheno.Observation;
//import org.molgenis.pheno.ObservationElement;
//import org.molgenis.pheno.ObservedValue;
//
///**
// * Sliceable version of the ObservationMatrix
// * 
// * 
// */
//public class SliceableObservationMatrix<R extends ObservationElement, C extends ObservationElement, V extends Observation>
//		extends AbstractObservationElementMatrix<R,C,V> implements SliceableMatrix<R, C, V>
//{
//	// optional in case of XGAP
//	// then valueClass must be TextDataElement or DecimalDataElement
//	private Data data;
//
//	/**
//	 * Construct sliceable matrix for one Data set.
//	 * 
//	 * @param database
//	 * @param data
//	 */
//	public SliceableObservationMatrix(Database database, Data data)
//	{
//		this.database = database;
//		this.data = data;
//		this.rowClass = (Class<R>) database.getClassForName(data
//				.getTargetType());
//		this.colClass = (Class<C>) database.getClassForName(data
//				.getFeatureType());
//		this.valueClass = (Class<V>) (data.getValueType().equals("Decimal") ? DecimalDataElement.class
//				: TextDataElement.class);
//
//	}
//
//	@Override
//	public SliceableMatrix<R, C, V> slice(MatrixQueryRule rule)
//			throws MatrixException
//	{
//		this.validate(rule);
//		switch (rule.getFilterType())
//		{
//		// row headers need to be refreshed in case of:
//			case rowIndex:
//				this.rowDirty = true;
//				break;
//			case rowHeader:
//				this.rowDirty = true;
//				break;
//			case colValues:
//				this.rowDirty = true;
//				break;
//			case colValueProperty:
//				this.rowDirty = true;
//				break;
//			// col headers need to be refreshed in case of:
//			case colIndex:
//				this.colDirty = true;
//				break;
//			case colHeader:
//				this.colDirty = true;
//				break;
//			case rowValues:
//				this.colDirty = true;
//				break;
//			case rowValueProperty:
//				this.rowDirty = true;
//				break;
//		}
//		rules.add(rule);
//		return this;
//	}
//
//	@Override
//	public List<R> getRowHeaders() throws MatrixException
//	{
//		// reload the rowheaders if filters have changed.
//		if (rowDirty)
//		{
//			try
//			{
//				Query<R> query = this.createSelectQuery(getRowClass());
//				this.rowHeaders = query.find();
//				rowDirty = false;
//			}
//			catch (Exception e)
//			{
//				throw new MatrixException(e);
//			}
//		}
//		return rowHeaders;
//	}
//
//	public Integer getRowCount() throws MatrixException
//	{
//		// fire a count query on headers
//		try
//		{
//			return this.createCountQuery(getRowClass()).count();
//		}
//		catch (DatabaseException e)
//		{
//			throw new MatrixException(e);
//		}
//	}
//
//	@Override
//	public List<Integer> getRowIndices() throws MatrixException
//	{
//		// retrieve the indices from the headers (we use the id value).
//		List<Integer> rowIndices = new ArrayList<Integer>();
//		for (R row : getRowHeaders())
//		{
//			rowIndices.add(row.getId());
//		}
//		return rowIndices;
//	}
//
//	@Override
//	public List<C> getColHeaders() throws MatrixException
//	{
//		// reload the rowheaders if filters have changed.
//		if (colDirty)
//		{
//			try
//			{
//				Query<C> query = this.createSelectQuery(getColClass());
//				this.colHeaders = query.find();
//				colDirty = false;
//			}
//			catch (Exception e)
//			{
//				throw new MatrixException(e);
//			}
//		}
//		return colHeaders;
//	}
//
//	@Override
//	public List<Integer> getColIndices() throws MatrixException
//	{
//		// get col indexes from col headers
//		List<Integer> colIndices = new ArrayList<Integer>();
//		for (C col : getColHeaders())
//		{
//			colIndices.add(col.getId());
//		}
//		return colIndices;
//	}
//
//	public Integer getColCount() throws MatrixException
//	{
//		// fire count query on col headers
//		try
//		{
//			return this.createCountQuery(getColClass()).count();
//		}
//		catch (DatabaseException e)
//		{
//			throw new MatrixException(e);
//		}
//	}
//
//	@Override
//	public BasicMatrix<R, C, V> getResult() throws Exception
//	{
//		throw new UnsupportedOperationException();
//	}
//
//	private <D extends ObservationElement> Query<D> createCountQuery(
//			Class<D> xClass) throws MatrixException
//	{
//		return this.createQuery(xClass, true);
//	}
//
//	private <D extends ObservationElement> Query<D> createSelectQuery(
//			Class<D> xClass) throws MatrixException
//	{
//		return this.createQuery(xClass, false);
//	}
//
//	/**
//	 * 
//	 * @param field
//	 *            , either ObservedValue.FEATURE or ObservedValue.TARGET
//	 * @throws MatrixException
//	 */
//	private <D extends ObservationElement> Query<D> createQuery(
//			Class<D> xClass, boolean countAll) throws MatrixException
//	{
//		// If xClass == getRowClass():
//		// A. filter on rowIndex + rowHeaderProperty
//		// B. filter on colValue: 1 subquery per column
//		// C. filter on rowOffset and rowLimit
//
//		try
//		{
//			// parameterize the refresh of the dim, either TARGET or FEATURE
//			String xDim = TextDataElement.TARGET;
//			MatrixQueryRule.Type xIndexFilterType = MatrixQueryRule.Type.rowIndex;
//			MatrixQueryRule.Type xHeaderFilterType = MatrixQueryRule.Type.rowHeader;
//			MatrixQueryRule.Type xValuesFilterType = MatrixQueryRule.Type.colValues;
//			MatrixQueryRule.Type xValuePropertyFilterType = MatrixQueryRule.Type.colValueProperty;
//			if (xClass.equals(getColClass()))
//			{
//				xDim = TextDataElement.FEATURE;
//				xIndexFilterType = MatrixQueryRule.Type.colIndex;
//				xHeaderFilterType = MatrixQueryRule.Type.colHeader;
//				xValuesFilterType = MatrixQueryRule.Type.rowValues;
//				xValuePropertyFilterType = MatrixQueryRule.Type.rowValueProperty;
//			}
//
//			// Impl
//
//			// Impl A: header query
//			Query<D> xQuery = database.query(xClass);
//			for (MatrixQueryRule rule : rules)
//			{
//				// rewrite rule(type=rowIndex) to rule(type=rowHeader, field=id)
//				if (rule.getFilterType().equals(xIndexFilterType))
//				{
//					rule.setField(ObservedValue.ID);
//					rule.setFilterType(xHeaderFilterType);
//				}
//				// add rowHeader filters to query / remember sort rules
//				if (rule.getFilterType().equals(xHeaderFilterType))
//				{
//					xQuery.addRules(rule);
//				}
//				// ignore all other rules
//			}
//
//			// select * from Individual where id in (select target from
//			// observedvalue where feature = 1 AND value > 10 AND target in
//			// (select target from observedvalue ));
//
//			// Impl B: create subquery per column, order matters because of
//			// sorting (not supported).
//			Map<Integer, Query<V>> subQueries = new LinkedHashMap<Integer, Query<V>>();
//			for (MatrixQueryRule rule : rules)
//			{
//				// only add colValues / rowValues as subquery
//				if (rule.getFilterType().equals(xValuePropertyFilterType))
//				{
//					// create a new subquery for each colValues column
//					if (subQueries.get(rule.getDimIndex()) == null)
//					{
//						Query<V> subQuery = database
//								.query(this.getValueClass());
//						// filter on data
//						if (data != null) subQuery.eq(TextDataElement.DATA,
//								data.getIdValue());
//						// filter on the column/row
//						subQuery.eq(xDim, rule.getDimIndex());
//						subQueries.put(rule.getDimIndex(), subQuery);
//					}
//					subQueries.get(rule.getDimIndex()).addRules(rule);
//				}
//				// ignore all other rules
//			}
//
//			// if no queries where made we still need one for the right 'data'
//			if (data != null && subQueries.size() == 0)
//			{
//				Query<V> subQuery = database.query(this.getValueClass());
//				// filter on data and first column
//				subQuery.eq(TextDataElement.DATA, data.getIdValue());
//				subQuery.eq(xDim, 0);
//				subQuery.sortASC(xDim + "Index");
//				subQueries.put(0, subQuery);
//			}
//			// add each subquery as condition on ID
//			for (Query<V> q : subQueries.values())
//			{
//				String sql = q.createFindSql();
//				// strip 'select ... from' and replace with 'select id from'
//				sql = "SELECT TextDataElement." + xDim + " "
//						+ sql.substring(sql.indexOf("FROM"));
//				// use QueryRule.Operator.IN_SUBQUERY
//				xQuery.subquery(ObservationElement.ID, sql);
//			}
//
//			// add limit and offset, unless count
//			if (!countAll)
//			{
//				if (xClass.equals(getColClass()))
//				{
//					xQuery.limit(colLimit);
//					xQuery.offset(colOffset);
//				}
//				else
//				{
//					xQuery.limit(rowLimit);
//					xQuery.offset(rowOffset);
//				}
//			}
//
//			return xQuery;
//		}
//		catch (Exception e)
//		{
//			throw new MatrixException(e);
//		}
//	}
//
//	private void validate(MatrixQueryRule rule) throws MatrixException
//	{
//		try
//		{
//			switch (rule.getFilterType())
//			{
//			// rowheader and colheader can do all operators
//				case rowHeader:
//					if (!this.getRowPropertyNames().contains(rule.getField()))
//					{
//						throw new MatrixException(
//								"rule.field not in matrix.rowPropertyNames: "
//										+ rule);
//					}
//					break;
//				case colHeader:
//					if (!this.getColPropertyNames().contains(rule.getField()))
//					{
//						throw new MatrixException(
//								"rule.field not in matrix.rowPropertyNames: "
//										+ rule);
//					}
//					break;
//				case rowValueProperty:
//					break;
//				case colValueProperty:
//					break;
//				default:
//					throw new MatrixException("rule not supported: " + rule);
//			}
//		}
//		catch (Exception e)
//		{
//			throw new MatrixException("rule not supported: " + rule);
//		}
//	}
//
//	@Override
//	public V[][] getValues() throws MatrixException
//	{
//		// get the indices (map to real coordinates)
//		final List<Integer> rowIndexes = getRowIndices();
//		final List<Integer> colIndexes = getColIndices();
//
//		// create matrix of suitable size
//		final V[][] valueMatrix = create(getRowLimit(), getColLimit(),
//				valueClass);
//
//		// retrieve values matching the selected indexes
//		Query<V> query = database.query(valueClass);
//		query.eq(TextDataElement.DATA, data.getIdValue());
//		query.in(TextDataElement.FEATURE, this.getColIndices());
//		query.in(TextDataElement.TARGET, this.getRowIndices());
//
//		// use the streaming interface?
//		List<V> values;
//		try
//		{
//			values = query.find();
//
//			for (V value : values)
//			{
//				valueMatrix[rowIndexes.indexOf(value.getTarget())][colIndexes
//						.indexOf(value.getFeature())] = value;
//			}
//
//			return valueMatrix;
//		}
//		catch (DatabaseException e)
//		{
//			throw new MatrixException(e);
//		}
//	}
//
//	@Override
//	public void refresh() throws MatrixException
//	{
//		this.reset();
//
//	}
//
//	@SuppressWarnings("unchecked")
//	protected V[] create(int rows)
//	{
//		return (V[]) new Object[rows];
//	}
//
//	@SuppressWarnings("unchecked")
//	public V[][] create(int rows, int cols, Class<V> valueType)
//	{
//		// create all empty rows as well
//		V[][] data = (V[][]) Array.newInstance(valueType, rows, cols);
//		for (int i = 0; i < data.length; i++)
//		{
//			data[i] = (V[]) Array.newInstance(valueType, cols);
//		}
//
//		return data;
//	}
//
//	@Override
//	public List<ObservedValue>[][] getValueLists() throws MatrixException
//	{
//		throw new UnsupportedOperationException("use getValues()");
//	}
//}
