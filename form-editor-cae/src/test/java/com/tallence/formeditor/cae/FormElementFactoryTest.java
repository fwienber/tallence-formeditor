/*
 * Copyright 2018 Tallence AG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tallence.formeditor.cae;

import com.tallence.formeditor.cae.elements.*;
import com.tallence.formeditor.cae.validator.InvalidGroupElementException;
import com.tallence.formeditor.contentbeans.FormEditor;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.*;

/**
 * Tests the parsing, serialization and validation of each form field.
 */
public class FormElementFactoryTest extends AbstractFormTest {


  @Autowired
  private FormElementFactory factory;

  private <T extends FormElement> T getTestFormElement(String id) {
    FormEditor article = getContentBean(2);
    return this.factory.createFormElement(article.getFormElements().getStruct(id), id);
  }

  @Test
  public void testTextField() {
    TextField formElement = getTestFormElement("TextField");

    assertThat(formElement, is(instanceOf(TextField.class)));
    formElement.setValue("123");
    assertThat(formElement.getValidationResult().size(), is(1));
    formElement.setValue("");
    assertThat(formElement.getValidationResult().size(), is(1));
    formElement.setValue("12345");
    assertTrue(formElement.getValidationResult().isEmpty());
  }


  @Test
  public void testNumberField() {
    NumberField formElement = getTestFormElement("NumberField");

    assertThat(formElement, is(instanceOf(NumberField.class)));
    formElement.setValue("abc");
    assertThat(formElement.getValidationResult().size(), is(1));
    formElement.setValue("2");
    assertThat(formElement.getValidationResult().size(), is(1));
    formElement.setValue("150");
    assertTrue(formElement.getValidationResult().isEmpty());
  }

  @Test
  public void testRadioButton() {
    RadioButtonGroup formElement = getTestFormElement("RadioButtonsMandatory");

    assertThat(formElement, is(instanceOf(RadioButtonGroup.class)));
    formElement.setValue("12345");
    assertTrue(formElement.getValidationResult().isEmpty());
    formElement.setValue("123");
    assertTrue(formElement.getValidationResult().isEmpty());
    assertThat(formElement.serializeValue(), is("123"));
    formElement.setValue("");
    assertThat(formElement.getValidationResult().size(), is(1));


    assertThat(formElement.getRadioButtons(), notNullValue());
    assertThat(formElement.getRadioButtons().get(1).isSelectedByDefault(), is(true));

  }

  @Test
  public void testOptionalRadioButton() {
    RadioButtonGroup formElement = getTestFormElement("RadioButtonsOptional");

    assertThat(formElement, is(instanceOf(RadioButtonGroup.class)));

    formElement.setValue(null);
    assertNull(formElement.serializeValue());

  }

  @Test (expected = InvalidGroupElementException.class)
  public void testRadioButtonInvalid() {
    RadioButtonGroup formElement = getTestFormElement("RadioButtonsEmptyValidator");
    //An Exception is expected here
    formElement.setValue("1234");
    assertThat(formElement.getValidationResult().size(), is(1));
  }

  @Test
  public void testCheckBoxes() {
    CheckBoxesGroup formElement = getTestFormElement("CheckBoxesMandatory");

    assertThat(formElement, is(instanceOf(CheckBoxesGroup.class)));
    formElement.setValue(Arrays.asList("12345", "123"));
    assertTrue(formElement.getValidationResult().isEmpty());
    formElement.setValue(Collections.emptyList());
    assertThat(formElement.getValidationResult().size(), is(1));

    assertThat(formElement.getCheckBoxes(), notNullValue());
    assertThat(formElement.getCheckBoxes().get(1).isSelectedByDefault(), is(true));

  }

  @Test (expected = InvalidGroupElementException.class)
  public void testCheckBoxesInvalid() {
    CheckBoxesGroup formElement = getTestFormElement("CheckBoxesEmptyValidator");
    //An Exception is expected here
    formElement.setValue(Arrays.asList("12", "123"));
    assertThat(formElement.getValidationResult().size(), is(1));
  }

  @Test
  public void testSelectBoxes() {
    SelectBox formElement = getTestFormElement("SelectBoxMandatory");

    assertThat(formElement, is(instanceOf(SelectBox.class)));
    formElement.setValue("123");
    assertTrue(formElement.getValidationResult().isEmpty());
    formElement.setValue(null);
    assertThat(formElement.getValidationResult().size(), is(1));

  }

  @Test (expected = InvalidGroupElementException.class)
  public void testSelectBoxesInvalid() {
    SelectBox formElement = getTestFormElement("SelectBoxEmptyValidator");
    //An Exception is expected here
    formElement.setValue("12");
    formElement.getValidationResult();
  }

  @Test
  public void testTextArea() {
    TextArea formElement = getTestFormElement("TextArea");

    assertThat(formElement, is(instanceOf(TextArea.class)));
    formElement.setValue("123");
    assertTrue(formElement.getValidationResult().isEmpty());
    formElement.setValue(null);
    assertThat(formElement.getValidationResult().size(), is(1));
    formElement.setValue("12345678900");
    assertThat(formElement.getValidationResult().size(), is(1));

    assertThat(formElement.getColumns(), is(4));
    assertThat(formElement.getRows(), is(5));
  }

  @Test
  public void testTextOnly() {
    TextOnly formElement = getTestFormElement("TextOnly");

    assertThat(formElement, is(instanceOf(TextOnly.class)));
    assertThat(formElement.getHint(), is("Das ist ein langer Text zur Erklärung des Formulars"));
    assertThat(formElement.getName(), is("TestName"));
  }
}
