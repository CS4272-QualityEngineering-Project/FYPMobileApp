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
    public void testReadDataFromFile_GeneralCase() {
        // Arrange
        byte[] expectedData = new byte[] {1, 2, 3, 4, 5};
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
    public void testSendWavDataToServer() {
        // TODO: Implement this test
    }

    @Test
    public void testHandleResponse() {
        // TODO: Implement this test
    }
}
