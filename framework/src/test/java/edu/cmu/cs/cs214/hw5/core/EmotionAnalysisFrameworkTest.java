package edu.cmu.cs.cs214.hw5.core;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import javax.swing.*;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class EmotionAnalysisFrameworkTest {
    private DataPlugin dataPlugin;
    private VisualPlugin visualPlugin;
    private EmotionAnalysisFrameworkListener listener;
    private JPanel panel;
    private EmotionAnalysisFrameworkImpl framework;

    private List<String> sources;
    private List<String> texts;
    private String text = "In the rugged Colorado Desert of California, there lies buried a treasure ship sailed " +
            "there hundreds of years ago by either Viking or Spanish explorers. Some say this is legend; " +
            "others insist it is fact. ";

    @Before
    public void setUp() throws Exception {
        sources = Collections.singletonList("source");
        texts = Collections.singletonList((text));

        // specfiy mock behavior
        listener = mock(EmotionAnalysisFrameworkListener.class);
        dataPlugin = mock(DataPlugin.class);
        visualPlugin = mock(VisualPlugin.class);
        panel = mock(JPanel.class);

        when(dataPlugin.getData(sources)).thenReturn(texts);
        when(visualPlugin.getVisual(any(EmotionAnalysis.class))).thenReturn(panel);


        // set listeners and plugins
        framework = new EmotionAnalysisFrameworkImpl();
        framework.setStateChangeListener(listener);
        framework.setCurrentVisualPlugin(visualPlugin);
        framework.setCurrentDataPlugin(dataPlugin);
    }

    @Test
    public void testRegisterDP() {
        ArgumentCaptor<DataPlugin> valueCapture = ArgumentCaptor.forClass(DataPlugin.class);
        doNothing().when(listener).onDataPluginRegistered(valueCapture.capture());

        framework.registerDataPlugin(dataPlugin);

        assertEquals(dataPlugin, valueCapture.getValue());
    }

    @Test
    public void testRegisterVP() {
        ArgumentCaptor<VisualPlugin> valueCapture = ArgumentCaptor.forClass(VisualPlugin.class);
        doNothing().when(listener).onVisualPluginRegistered(valueCapture.capture());

        framework.registerVisualPlugin(visualPlugin);
        assertEquals(visualPlugin, valueCapture.getValue());
    }


    @Test
    public void testRequestData() throws Exception {
        framework.requestDataFromSource(sources);

        // test that requesting data calls the listener
        verify(listener, times(1)).onDataRequest();
        verify(listener, times(1)).onDataReadyToDisplay();
    }

    @Test
    public void testVisualizeData() throws Exception {
        framework.requestDataFromSource(sources);
        framework.requestVisualizeData();

        // test that visualizing data calls the listener
        verify(listener, times(2)).onDataRequest();
        verify(listener, times(1)).onLoadVisualRequest(Mockito.any(JPanel.class));

    }

}
