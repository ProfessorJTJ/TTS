package anywheresoftware.b4a.obejcts;

import java.util.Locale;
import java.util.Set;

import anywheresoftware.b4a.BA;
import android.speech.tts.Voice;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.EngineInfo;
import android.speech.tts.UtteranceProgressListener;
import anywheresoftware.b4a.AbsObjectWrapper;
import anywheresoftware.b4a.objects.collections.List;

@BA.Version(1.2f)
@BA.ShortName("TTS")
public class TTS extends AbsObjectWrapper<TextToSpeech> {
	private BA _ba = null; 
	private String _EventName = null;
	
    public void Initialize(final BA ba, final String EventName) {
    	_ba = ba;
    	_EventName = EventName;
        final TextToSpeech tts = new TextToSpeech(ba.context, (TextToSpeech.OnInitListener)new TextToSpeech.OnInitListener() {
            public void onInit(final int status) {
                ba.raiseEventFromUI((Object)null, String.valueOf(EventName.toLowerCase(BA.cul)) + "_ready", new Object[] { status == 0 });
            }
        });
        setListerner(tts);
        this.setObject(tts);
    }
    
    private void Initialize(final BA ba, final String EventName, final String CustomEngine) {
        final TextToSpeech tts = new TextToSpeech(ba.context, (TextToSpeech.OnInitListener)new TextToSpeech.OnInitListener() {
            public void onInit(final int status) {
                ba.raiseEventFromUI((Object)null, String.valueOf(EventName.toLowerCase(BA.cul)) + "_ready", new Object[] { status == 0 });
            }
        }, CustomEngine);
        setListerner(tts);
        this.setObject(tts);
    }
    
    public int Speak(final String Text, final boolean ClearQueue, final String UtteranceId) {
        final int r = ((TextToSpeech)this.getObject()).speak(Text, (int)(ClearQueue ? 0 : 1), null, UtteranceId);
        return r;
    }
    
    public void Stop() {
        ((TextToSpeech)this.getObject()).stop();
    }
    
    public void setPitch(final float value) {
        ((TextToSpeech)this.getObject()).setPitch(value);
    }
    
    public void setSpeechRate(final float value) {
        ((TextToSpeech)this.getObject()).setSpeechRate(value);
    }
    
    public boolean SetLanguage(final String Language, final String Country) {
        Locale l;
        if (Country.length() > 0) {
            l = new Locale(Language, Country);
        }
        else {
            l = new Locale(Language);
        }
        final int r = ((TextToSpeech)this.getObject()).setLanguage(l);
        return r >= 0;
    }
    
    public boolean isSpeaking() {
        return ((TextToSpeech)this.getObject()).isSpeaking();
    }
    
    public List GetVoices()
    {
    	Set<Voice> voices = ((TextToSpeech)this.getObject()).getVoices();
    	List voicesList = new List();
    	voicesList.Initialize();
    	
    	for(Voice eachVoice : voices)
    	{
    		voicesList.Add(eachVoice.getName());
    	}
    	return voicesList;
    }
    
    public boolean SetVoice(final String VoiceName)
    {
    	Set<Voice> voices = ((TextToSpeech)this.getObject()).getVoices();
    	for (Voice eachVoice : voices) {
    		if (eachVoice.getName().equalsIgnoreCase(VoiceName))
    		{
    			return (((TextToSpeech)this.getObject()).setVoice(eachVoice) == 0);
    		}
        }
    	return false;
    }
    
    public List GetEngines()
    {
    	java.util.List<EngineInfo> engines = ((TextToSpeech)this.getObject()).getEngines();
    	List enginesList = new List();
    	enginesList.Initialize();
    	
    	for(EngineInfo eachEngine : engines)
    	{
    		enginesList.Add(eachEngine.name);
    	}
    	return enginesList;
    }
    
    public boolean SetEngine(final String EngineName)
    {
    	if (_EventName != null)
    	{
    		java.util.List<EngineInfo> engines = ((TextToSpeech)this.getObject()).getEngines();
        	for (EngineInfo eachEngine : engines) {
        		if (eachEngine.name.equalsIgnoreCase(EngineName))
        		{
        			((TextToSpeech)this.getObject()).shutdown();
        			Initialize(_ba, _EventName, EngineName);
        			return true;
        		}
            }
    	}
    	return false;
    }

    
    public void Release() {
        if (this.IsInitialized()) {
            ((TextToSpeech)this.getObject()).shutdown();
            this.setObject(null);
        }
    }
    
    private void setListerner(TextToSpeech tts)
    {
    	if (tts.getEngines().size() > 0)
        {
        	tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onDone(String utteranceId) {
                    _ba.raiseEventFromUI((Object)null, String.valueOf(_EventName.toLowerCase(BA.cul)) + "_finish", new Object[] { utteranceId });
                }
        		@Override
                public void onError(String utteranceId) {
                }
        		@Override
                public void onStart(String utteranceId) {
                }
            });
        }
    }
}
