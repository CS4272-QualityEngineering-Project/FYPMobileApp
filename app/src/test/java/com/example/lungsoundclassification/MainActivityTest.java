package com.example.lungsoundclassification;

import android.content.ContentResolver;
import android.net.Uri;
import android.content.Context;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.RequestBody;

@RunWith(MockitoJUnitRunner.class)
public class MainActivityTest {

    @Mock
    Context mockContext;

    @Mock
    Uri mockUri;

    @Mock
    private ContentResolver mockContentResolver;

    @Mock
    private InputStream mockInputStream;


    @Test
    public void testIsFileAccessible_FileAccessible_ReturnsTrue() {
        // Arrange
        try {
            when(mockContext.getContentResolver()).thenReturn(mockContentResolver);
            when(mockContentResolver.openInputStream(mockUri)).thenReturn(mockInputStream);
            when(mockInputStream.available()).thenReturn(1);
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("IO Exception");
            e.printStackTrace();
        }

        // Act
        boolean result = MainActivity.isFileAccessible(mockUri, mockContext);

        // Assert
        assertTrue(result);
    }

    @Test
    public void testIsFileAccessible_FileNotAccessible_ReturnsFalse() {
        // Arrange
        try {
            when(mockContext.getContentResolver()).thenReturn(mockContentResolver);
            when(mockContentResolver.openInputStream(mockUri)).thenReturn(mockInputStream);
            when(mockInputStream.available()).thenReturn(0);
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("IO Exception");
            e.printStackTrace();
        }

        // Act
        boolean result = MainActivity.isFileAccessible(mockUri, mockContext);

        // Assert
        assertFalse(result);
    }

    @Test
    public void testIsFileAccessible_IOException_ReturnsFalse() {
        // Arrange
        try {
            when(mockContext.getContentResolver()).thenReturn(mockContentResolver);
            when(mockContentResolver.openInputStream(mockUri)).thenReturn(mockInputStream);
            when(mockInputStream.available()).thenThrow(new IOException());
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("IO Exception");
            e.printStackTrace();
        }

        // Act
        boolean result = MainActivity.isFileAccessible(mockUri, mockContext);

        // Assert
        assertFalse(result);
    }

    @Test
    public void testReadDataFromFile_GeneralCase() {
        // Arrange
        byte[] expectedData = new byte[]{1, 2, 3, 4, 5};
        InputStream mockInputStream = new ByteArrayInputStream(expectedData);

        try {
            when(mockContext.getContentResolver()).thenReturn(mockContentResolver);
            when(mockContentResolver.openInputStream(mockUri)).thenReturn(mockInputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Act
        byte[] actualData = MainActivity.readDataFromFile(mockUri, mockContext);

        // Assert
        assertArrayEquals(expectedData, actualData);
    }

    @Test
    public void testReadDataFromFile_EmptyFile() {
        // Arrange
        byte[] expectedData = new byte[0];
        InputStream mockInputStream = new ByteArrayInputStream(expectedData);

        try {
            when(mockContext.getContentResolver()).thenReturn(mockContentResolver);
            when(mockContentResolver.openInputStream(mockUri)).thenReturn(mockInputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Act
        byte[] actualData = MainActivity.readDataFromFile(mockUri, mockContext);

        // Assert
        assertArrayEquals(expectedData, actualData);
    }

    @Test
    public void testReadDataFromFile_IOException_ReturnsNull() {
        // Arrange
        try {
            when(mockContext.getContentResolver()).thenReturn(mockContentResolver);
            when(mockContentResolver.openInputStream(mockUri)).thenReturn(mockInputStream);
            when(mockInputStream.read(any())).thenThrow(new IOException());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Act
        byte[] actualData = MainActivity.readDataFromFile(mockUri, mockContext);

        // Assert
        assertNull(actualData);
    }

    @Test
    public void testCreateRequestBody()  {
        // Arrange
        byte[] wavData = new byte[]{1,2,3,4,5};
        try {
            when(mockContext.getContentResolver()).thenReturn(mockContentResolver);
            when(mockContentResolver.getType(mockUri)).thenReturn("audio/x-wav");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Act
        RequestBody requestBody = MainActivity.createRequestBody(wavData, mockUri, mockContext);

        //Assert
        try {
        assertEquals(wavData.length, requestBody.contentLength());
        assertEquals("audio/x-wav", requestBody.contentType().toString());
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void  testCreateRequestBody_notAudioType()  {
        // Arrange
        byte[] wavData = new byte[]{1,2,3,4,5};
        try {
            when(mockContext.getContentResolver()).thenReturn(mockContentResolver);
            when(mockContentResolver.getType(mockUri)).thenReturn(null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Act
        RequestBody requestBody = MainActivity.createRequestBody(wavData, mockUri, mockContext);

        //Assert
        assertNull(requestBody);
    }

    @Test
    public void  testCreateRequestBody_notAudioData()  {
        // Arrange

        // Act
        RequestBody requestBody = MainActivity.createRequestBody(null, mockUri, mockContext);

        //Assert
        assertNull(requestBody);
    }
}
