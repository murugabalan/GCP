package com.example.demo.controller;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.checkerframework.checker.lock.qual.NewObject;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.gax.paging.Page;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQuery.TableDataListOption;
import com.google.cloud.bigquery.BigQueryException;
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.bigquery.Dataset;
import com.google.cloud.bigquery.DatasetId;
import com.google.cloud.bigquery.DatasetInfo;
import com.google.cloud.bigquery.Field;
import com.google.cloud.bigquery.FieldValueList;
import com.google.cloud.bigquery.InsertAllRequest;
import com.google.cloud.bigquery.InsertAllResponse;
import com.google.cloud.bigquery.Job;
import com.google.cloud.bigquery.JobException;
import com.google.cloud.bigquery.JobInfo;
import com.google.cloud.bigquery.QueryJobConfiguration;
import com.google.cloud.bigquery.Schema;
import com.google.cloud.bigquery.StandardSQLTypeName;
import com.google.cloud.bigquery.StandardTableDefinition;
import com.google.cloud.bigquery.Table;
import com.google.cloud.bigquery.TableDataWriteChannel;
import com.google.cloud.bigquery.TableDefinition;
import com.google.cloud.bigquery.TableId;
import com.google.cloud.bigquery.TableInfo;
import com.google.cloud.bigquery.TableResult;

@RestController
@RequestMapping("/bigQuery")
public class BqController {
	String projectId = "muthu-training-2024";
	
	@PostMapping("/dataset")
	public void createDataset(@RequestParam String datasetName) {
		try {
			BigQuery bigquery = BigQueryOptions.getDefaultInstance().getService();
			DatasetInfo datasetInfo = DatasetInfo.newBuilder(datasetName).build();
			Dataset newDataset = bigquery.create(datasetInfo);
			String newDatasetName = newDataset.getDatasetId().getDataset();
			System.out.println(newDatasetName + " created successfully");
		} catch (BigQueryException e) {
			System.out.println("Dataset was not created. \n" + e.toString());
		}
	}

	@PostMapping("/createTable")
	public void createTable(@RequestParam String datasetName, @RequestParam String tableName) {
		try {
			Schema schema = Schema.of(Field.of("id", StandardSQLTypeName.STRING),
					Field.of("name", StandardSQLTypeName.STRING), Field.of("dob", StandardSQLTypeName.STRING));
			BigQuery bigquery = BigQueryOptions.getDefaultInstance().getService();
			TableId tableId = TableId.of(datasetName, tableName);
			TableDefinition tableDefinition = StandardTableDefinition.of(schema);
			TableInfo tableInfo = TableInfo.newBuilder(tableId, tableDefinition).build();
			bigquery.create(tableInfo);
			System.out.println("Table created successfully");
		} catch (BigQueryException e) {
			System.out.println("Table was not created. \n" + e.toString());
		}
	}

	@PostMapping("/insertDataInsideTable")
	public void insertDataInTable(@RequestParam String datasetName, @RequestParam String tableName,
			@RequestBody Map<String, Object> data) {
		try {
			BigQuery bigquery = BigQueryOptions.getDefaultInstance().getService();
			Dataset dataset = bigquery.getDataset(DatasetId.of(projectId, datasetName));
			TableId table = TableId.of(datasetName, tableName);
			TableId tableId = TableId.of(projectId, dataset.getDatasetId().getDataset(), table.getTable());
			InsertAllRequest.RowToInsert rowToInsert = InsertAllRequest.RowToInsert.of(data);
			bigquery.insertAll(InsertAllRequest.newBuilder(tableId).addRow(rowToInsert).build());
			System.out.println("Data inserted");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@GetMapping("/listDataInsideTable")
	public void listData(@RequestParam String dataset, @RequestParam String table) {
		try {
			BigQuery bigquery = BigQueryOptions.getDefaultInstance().getService();
			TableId tableId = TableId.of(dataset, table);
			TableResult result = bigquery.listTableData(tableId, TableDataListOption.pageSize(100));
			result.iterateAll().forEach(row -> {
				List<String> fieldValues = new ArrayList<>();
				row.forEach(fieldValue -> fieldValues.add(fieldValue.getValue().toString()));
				System.out.println(String.join(", ", fieldValues));
			});
			System.out.println("Query ran successfully");
		} catch (BigQueryException e) {
			System.out.println("Query failed to run \n" + e.toString());
		}
	}
	
	@PutMapping("/updateDataInTable")
	public String updateData(@RequestParam String datasetName, @RequestParam String tableName,
			@RequestBody String data) {
		try {
			BigQuery bigquery = BigQueryOptions.getDefaultInstance().getService();
			String id = null;
			ObjectMapper obj = new ObjectMapper();
			JsonNode node = obj.readTree(data);
			id = node.get("id").toString();
			String name = node.get("name").toString();
			String query = String.format("UPDATE  %s.%s.%s SET name = '%s' WHERE id = %s", projectId, datasetName,
					tableName, name, id);
			System.out.println(query);
			QueryJobConfiguration queryConfig = QueryJobConfiguration.newBuilder(query).build();
			Job queryJob = bigquery.create(JobInfo.of(queryConfig));
			try {
				queryJob = queryJob.waitFor();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				return e.getMessage();
			}
			if (queryJob != null && queryJob.getStatus().getError() == null) {
				return "Data updated successfully.";
			} else {
				return "Error occurred while updating data: " + queryJob.getStatus().getError();
			}
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	@DeleteMapping("/deleteData")
	public String deleteData(String datasetName, String tableName, @RequestParam String id)
			throws InterruptedException {
		BigQuery bigquery = BigQueryOptions.getDefaultInstance().getService();
		String deleteQuery = String.format("DELETE FROM `%s.%s.%s` WHERE id = %s", projectId, datasetName, tableName,
				id);
		QueryJobConfiguration queryConfig = QueryJobConfiguration.newBuilder(deleteQuery).build();
		Job queryJob = bigquery.create(JobInfo.of(queryConfig));
		queryJob = queryJob.waitFor();
		if (queryJob != null && queryJob.getStatus().getError() == null) {
			return "Data deleted successfully.";
		} else {
			return "Error occurred while deleting data: " + queryJob.getStatus().getError();
		}
	}
}