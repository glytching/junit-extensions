/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.glytching.junit.extension.random;

import java.util.List;

public class DomainObject {

  private int id;
  private String name;
  private long value;
  private double price;
  private NestedDomainObject nestedDomainObject;
  private List<String> wotsits;

  public DomainObject() {}

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public long getValue() {
    return value;
  }

  public void setValue(long value) {
    this.value = value;
  }

  public double getPrice() {
    return price;
  }

  public void setPrice(double price) {
    this.price = price;
  }

  public NestedDomainObject getNestedDomainObject() {
    return nestedDomainObject;
  }

  public void setNestedDomainObject(NestedDomainObject nestedDomainObject) {
    this.nestedDomainObject = nestedDomainObject;
  }

  public List<String> getWotsits() {
    return wotsits;
  }

  public void setWotsits(List<String> wotsits) {
    this.wotsits = wotsits;
  }
}
