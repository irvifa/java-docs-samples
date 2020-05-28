/*
 * Copyright 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.firestore.snippets;

import com.example.firestore.snippets.model.City;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.SetOptions;
import com.google.cloud.firestore.WriteBatch;
import com.google.cloud.firestore.WriteResult;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/** Snippets to demonstrate Firestore add, update and delete operations. */
class ManageDataSnippets {

  private final Firestore database;

  ManageDataSnippets(Firestore database) {
    this.database = database;
  }

  /**
   * Add a document to a collection using a map.
   *
   * @return document data
   */
  Map<String, Object> addSimpleDocumentAsMap() throws Exception {
    // [START fs_add_doc_as_map]
    // Create a Map to store the data we want to set
    Map<String, Object> docData = new HashMap<>();
    docData.put("name", "Los Angeles");
    docData.put("state", "CA");
    docData.put("country", "USA");
    docData.put("regions", Arrays.asList("west_coast", "socal"));
    // Add a new document (asynchronously) in collection "cities" with id "LA"
    ApiFuture<WriteResult> future = database.collection("cities").document("LA").set(docData);
    // ...
    // future.get() blocks on response
    System.out.println("Update time : " + future.get().getUpdateTime());
    // [END fs_add_doc_as_map]
    return docData;
  }

  /**
   * Add a document to a collection using a map with different supported data types.
   *
   * @return document data
   */
  Map<String, Object> addDocumentWithDifferentDataTypes() throws Exception {
    // [START fs_add_doc_data_types]
    Map<String, Object> docData = new HashMap<>();
    docData.put("stringExample", "Hello, World");
    docData.put("booleanExample", false);
    docData.put("numberExample", 3.14159265);
    docData.put("nullExample", null);

    ArrayList<Object> arrayExample = new ArrayList<>();
    Collections.addAll(arrayExample, 5L, true, "hello");
    docData.put("arrayExample", arrayExample);

    Map<String, Object> objectExample = new HashMap<>();
    objectExample.put("a", 5L);
    objectExample.put("b", true);

    docData.put("objectExample", objectExample);

    ApiFuture<WriteResult> future = database.collection("data").document("one").set(docData);
    System.out.println("Update time : " + future.get().getUpdateTime());
    // [END fs_add_doc_data_types]

    return docData;
  }

  /**
   * Add a document to a collection as a custom object.
   *
   * @return entity added
   */
  City addSimpleDocumentAsEntity() throws Exception {
    // [START fs_add_simple_doc_as_entity]
    City city = new City("Los Angeles", "CA", "USA", false, 3900000L,
        Arrays.asList("west_coast", "socal"));
    ApiFuture<WriteResult> future = database.collection("cities").document("LA").set(city);
    // block on response if required
    System.out.println("Update time : " + future.get().getUpdateTime());
    // [END fs_add_simple_doc_as_entity]

    return city;
  }

  /**
   * set() providing a document ID.
   */
  void setRequiresId(Map<String, String> data) {
    // [START fs_set_requires_id]
    database.collection("cities").document("new-city-id").set(data);
    // [END fs_set_requires_id]
  }

  /**
   * Add a document without explicitly providing the document id. The document id gets automatically
   * generated.
   *
   * @return auto generated id
   */
  String addDocumentDataWithAutoGeneratedId() throws Exception {
    // [START fs_add_doc_data_with_auto_id]
    // Add document data with auto-generated id.
    Map<String, Object> data = new HashMap<>();
    data.put("name", "Tokyo");
    data.put("country", "Japan");
    ApiFuture<DocumentReference> addedDocRef = database.collection("cities").add(data);
    System.out.println("Added document with ID: " + addedDocRef.get().getId());
    // [END fs_add_doc_data_with_auto_id]

    return addedDocRef.get().getId();
  }

  /**
   * Add data to a document after generating the document id.
   *
   * @return auto generated id
   */
  String addDocumentDataAfterAutoGeneratingId() throws Exception {
    City data = new City();

    // [START fs_add_doc_data_after_auto_id]
    // Add document data after generating an id.
    DocumentReference addedDocRef = database.collection("cities").document();
    System.out.println("Added document with ID: " + addedDocRef.getId());

    // later...
    ApiFuture<WriteResult> writeResult = addedDocRef.set(data);
    // [END fs_add_doc_data_after_auto_id]

    // writeResult.get() blocks on operation
    System.out.println("Update time : " + writeResult.get().getUpdateTime());
    return addedDocRef.getId();
  }

  /** Partially update a document using the .update(field1, value1..) method. */
  void updateSimpleDocument() throws Exception {
    database.collection("cities").document("DC").set(new City("Washington D.C.")).get();
    // [START fs_update_doc]
    // Update an existing document
    DocumentReference docRef = database.collection("cities").document("DC");

    // (async) Update one field
    ApiFuture<WriteResult> future = docRef.update("capital", true);

    // ...
    WriteResult result = future.get();
    System.out.println("Write result: " + result);
    // [END fs_update_doc]
  }

  /** Partially update fields of a document using a map (field => value). */
  void updateUsingMap() throws Exception {
    database.collection("cities").document("DC").set(new City("Washington D.C.")).get();
    // [START fs_update_doc_map]
    // update multiple fields using a map
    DocumentReference docRef = database.collection("cities").document("DC");

    Map<String, Object> updates = new HashMap<>();
    updates.put("name", "Washington D.C.");
    updates.put("country", "USA");
    updates.put("capital", true);

    //asynchronously update doc
    ApiFuture<WriteResult> writeResult = docRef.update(updates);
    // ...
    System.out.println("Update time : " + writeResult.get().getUpdateTime());
    // [END fs_update_doc_map]
  }

  /** Partially update fields of a document using a map (field => value). */
  void updateAndCreateIfMissing() throws Exception {
    // [START fs_update_create_if_missing]
    //asynchronously update doc, create the document if missing
    Map<String, Object> update = new HashMap<>();
    update.put("capital", true);

    ApiFuture<WriteResult> writeResult =
        database
            .collection("cities")
            .document("BJ")
            .set(update, SetOptions.merge());
    // ...
    System.out.println("Update time : " + writeResult.get().getUpdateTime());
    // [END fs_update_create_if_missing]
  }

  /** Partial update nested fields of a document. */
  void updateNestedFields() throws Exception {
    //CHECKSTYLE OFF: VariableDeclarationUsageDistance
    // [START fs_update_nested_fields]
    // Create an initial document to update
    DocumentReference frankDocRef = database.collection("users").document("frank");
    Map<String, Object> initialData = new HashMap<>();
    initialData.put("name", "Frank");
    initialData.put("age", 12);

    Map<String, Object> favorites = new HashMap<>();
    favorites.put("food", "Pizza");
    favorites.put("color", "Blue");
    favorites.put("subject", "Recess");
    initialData.put("favorites", favorites);

    ApiFuture<WriteResult> initialResult = frankDocRef.set(initialData);
    // Confirm that data has been successfully saved by blocking on the operation
    initialResult.get();

    // Update age and favorite color
    Map<String, Object> updates = new HashMap<>();
    updates.put("age", 13);
    updates.put("favorites.color", "Red");

    // Async update document
    ApiFuture<WriteResult> writeResult = frankDocRef.update(updates);
    // ...
    System.out.println("Update time : " + writeResult.get().getUpdateTime());
    // [END fs_update_nested_fields]
    //CHECKSTYLE ON: VariableDeclarationUsageDistance
  }

  /** Update document with server timestamp. */
  void updateServerTimestamp() throws Exception {
    database.collection("objects").document("some-id").set(new HashMap<String, Object>()).get();

    // [START fs_update_server_timestamp]
    DocumentReference docRef = database.collection("objects").document("some-id");
    // Update the timestamp field with the value from the server
    ApiFuture<WriteResult> writeResult = docRef.update("timestamp", FieldValue.serverTimestamp());
    System.out.println("Update time : " + writeResult.get());
    // [END fs_update_server_timestamp]
  }

  /** Update array fields in a document. **/
  void updateDocumentArray() throws Exception {
    // [START fs_update_document_array]
    DocumentReference washingtonRef = database.collection("cities").document("DC");

    // Atomically add a new region to the "regions" array field.
    ApiFuture<WriteResult> arrayUnion = washingtonRef.update("regions",
        FieldValue.arrayUnion("greater_virginia"));
    System.out.println("Update time : " + arrayUnion.get());

    // Atomically remove a region from the "regions" array field.
    ApiFuture<WriteResult> arrayRm = washingtonRef.update("regions",
        FieldValue.arrayRemove("east_coast"));
    System.out.println("Update time : " + arrayRm.get());
    // [END fs_update_document_array]
  }

  /** Delete specific fields when updating a document. */
  void deleteFields() throws Exception {
    City city = new City("Beijing");
    city.setCapital(true);
    database.collection("cities").document("BJ").set(city).get();

    // [START fs_delete_fields]
    DocumentReference docRef = database.collection("cities").document("BJ");
    Map<String, Object> updates = new HashMap<>();
    updates.put("capital", FieldValue.delete());
    // Update and delete the "capital" field in the document
    ApiFuture<WriteResult> writeResult = docRef.update(updates);
    System.out.println("Update time : " + writeResult.get());
    // [END fs_delete_fields]
  }

  /** Delete a document in a collection. */
  void deleteDocument() throws Exception {
    database.collection("cities").document("DC").set(new City("Washington, D.C.")).get();
    // [START fs_delete_doc]
    // asynchronously delete a document
    ApiFuture<WriteResult> writeResult = database.collection("cities").document("DC").delete();
    // ...
    System.out.println("Update time : " + writeResult.get().getUpdateTime());
    // [END fs_delete_doc]
  }

  // [START fs_delete_collection]
  /** Delete a collection in batches to avoid out-of-memory errors.
   * Batch size may be tuned based on document size (atmost 1MB) and application requirements.
   */
  void deleteCollection(CollectionReference collection, int batchSize) {
    try {
      // retrieve a small batch of documents to avoid out-of-memory errors
      ApiFuture<QuerySnapshot> future = collection.limit(batchSize).get();
      int deleted = 0;
      // future.get() blocks on document retrieval
      List<QueryDocumentSnapshot> documents = future.get().getDocuments();
      for (QueryDocumentSnapshot document : documents) {
        document.getReference().delete();
        ++deleted;
      }
      if (deleted >= batchSize) {
        // retrieve and delete another batch
        deleteCollection(collection, batchSize);
      }
    } catch (Exception e) {
      System.err.println("Error deleting collection : " + e.getMessage());
    }
  }
  // [END fs_delete_collection]

  /** Run a simple transaction to perform a field value increment.
   *
   * @return transaction future
   */
  ApiFuture<Void> runSimpleTransaction() throws Exception {
    // [START fs_run_simple_transaction]
    // Initialize doc
    final DocumentReference docRef = database.collection("cities").document("SF");
    City city = new City("SF");
    city.setCountry("USA");
    city.setPopulation(860000L);
    docRef.set(city).get();

    // run an asynchronous transaction
    ApiFuture<Void> futureTransaction = database.runTransaction(transaction -> {
      // retrieve document and increment population field
      DocumentSnapshot snapshot = transaction.get(docRef).get();
      long oldPopulation = snapshot.getLong("population");
      transaction.update(docRef, "population", oldPopulation + 1);
      return null;
    });
    // block on transaction operation using transaction.get()
    // [END fs_run_simple_transaction]
    return futureTransaction;
  }

  /**
   * Return information from a conditional transaction.
   *
   *
   * @param population : set initial population.
   */
  String returnInfoFromTransaction(long population) throws Exception {
    Map<String, Object> map = new HashMap<>();
    map.put("population", population);
    // Block until transaction is complete is using transaction.get()
    database.collection("cities").document("SF").set(map).get();
    // [START fs_return_info_transaction]
    final DocumentReference docRef = database.collection("cities").document("SF");
    ApiFuture<String> futureTransaction = database.runTransaction(transaction -> {
      DocumentSnapshot snapshot = transaction.get(docRef).get();
      Long newPopulation = snapshot.getLong("population") + 1;
      // conditionally update based on current population
      if (newPopulation <= 1000000L) {
        transaction.update(docRef, "population", newPopulation);
        return "Population increased to " + newPopulation;
      } else {
        throw new Exception("Sorry! Population is too big.");
      }
    });
    // Print information retrieved from transaction
    System.out.println(futureTransaction.get());
    // [END fs_return_info_transaction]
    return futureTransaction.get();
  }

  /** Write documents in a batch. */
  void writeBatch() throws Exception {
    database.collection("cities").document("SF").set(new City()).get();
    database.collection("cities").document("LA").set(new City()).get();

    // [START fs_write_batch]
    // Get a new write batch
    WriteBatch batch = database.batch();

    // Set the value of 'NYC'
    DocumentReference nycRef = database.collection("cities").document("NYC");
    batch.set(nycRef, new City());

    // Update the population of 'SF'
    DocumentReference sfRef = database.collection("cities").document("SF");
    batch.update(sfRef, "population", 1000000L);

    // Delete the city 'LA'
    DocumentReference laRef = database.collection("cities").document("LA");
    batch.delete(laRef);

    // asynchronously commit the batch
    ApiFuture<List<WriteResult>> future = batch.commit();
    // ...
    // future.get() blocks on batch commit operation
    for (WriteResult result :future.get()) {
      System.out.println("Update time : " + result.getUpdateTime());
    }
    // [END fs_write_batch]
  }

  public void updateDocumentIncrement() throws ExecutionException, InterruptedException {
    final City city = new City();
    city.setPopulation(100L);
    database.collection("cities").document("DC").set(city).get();

    // [START fs_update_document_increment]
    DocumentReference washingtonRef = database.collection("cities").document("DC");

    // Atomically increment the population of the city by 50.
    final ApiFuture<WriteResult> updateFuture = washingtonRef
        .update("population", FieldValue.increment(50));
    // [END fs_update_document_increment]
    updateFuture.get();
  }

}
