package com.example.lungsoundclassification;

import android.content.ContentResolver;
import android.net.Uri;
import android.content.Context;
import android.os.ParcelFileDescriptor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.FileDescriptor;
import java.io.FileInputStream;
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
    public void testReadDataFromFile() {
        // TODO: Implement this test
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
