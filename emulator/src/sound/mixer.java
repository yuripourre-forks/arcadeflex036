
package sound;

import static mame.mame.*;
import static sound.mixerH.*;
import static mame.driverH.*;
import static arcadeflex.sound.*;
import static arcadeflex.libc_old.*;
import static mame.sndintrf.*;

public class mixer {
    /* enable this to turn off clipping (helpful to find cases where we max out */
    public static final int DISABLE_CLIPPING		=0;
    
    /* accumulators have ACCUMULATOR_SAMPLES samples (must be a power of 2) */
    public static final int ACCUMULATOR_SAMPLES		=8192;
    public static final int ACCUMULATOR_MASK		=(ACCUMULATOR_SAMPLES - 1);
    
    /* fractional numbers have FRACTION_BITS bits of resolution */
    public static final int FRACTION_BITS			=16;
    public static final int FRACTION_MASK			=((1 << FRACTION_BITS) - 1);
    
    
    static int mixer_sound_enabled;
    
    
    /* holds all the data for the a mixer channel */
    public static class mixer_channel_data
    {
        String		name;

    	/* current volume, gain and pan */
        int		volume;
        int		gain;
    	int		pan;

	/* mixing levels */
    	char /*UINT8*/		mixing_level;
    	char /*UINT8*/		default_mixing_level;
    	char /*UINT8*/		config_mixing_level;
    	char /*UINT8*/		config_default_mixing_level;
    
    	/* current playback positions */
    	int/*UINT32*/		input_frac;
    	int/*UINT32*/		samples_available;
        int /*UINT32*/		frequency;
        int/*UINT32*/		step_size;
   
        /* state of non-streamed playback */
    	boolean/*UINT8*/	is_stream;
        boolean/*UINT8*/		is_playing;
        boolean/*UINT8*/		is_looping;
        boolean/*UINT8*/		is_16bit;

        CharPtr data_start;
        int data_end;
        int data_current;
    };
    
    
    
    /* channel data */
    static mixer_channel_data[] mixer_channel = new mixer_channel_data[MIXER_MAX_CHANNELS];
    static /*UINT8*/char[] config_mixing_level = new char[MIXER_MAX_CHANNELS];
    static /*UINT8*/char[] config_default_mixing_level = new char[MIXER_MAX_CHANNELS];

    static /*UINT8*/char first_free_channel = 0;
    static /*UINT8*/char config_invalid;
    static boolean is_stereo;
    
    /* 32-bit accumulators */
    static int/*UINT32*/ accum_base;
    static int[] left_accum = new int[ACCUMULATOR_SAMPLES];
    static int[] right_accum = new int[ACCUMULATOR_SAMPLES];
        
    
    /* 16-bit mix buffers */
    static short[] mix_buffer = new short[ACCUMULATOR_SAMPLES * 2]; /* *2 for stereo */

    /* global sample tracking */
    static int/*UINT32*/ samples_this_frame;
        
    /***************************************************************************
    	mixer_sh_start
    ***************************************************************************/
    
    public static int mixer_sh_start()
    {    
    	/* reset all channels to their defaults */
        for (int i = 0; i < mixer_channel.length; i++)
        {
                mixer_channel[i] = new mixer_channel_data();
                mixer_channel[i].mixing_level = 0xff;
                mixer_channel[i].default_mixing_level = 0xff;
                mixer_channel[i].config_mixing_level = config_mixing_level[i];
                mixer_channel[i].config_default_mixing_level = config_default_mixing_level[i];
        }
  
    	/* determine if we're playing in stereo or not */
    	first_free_channel = 0;
    	is_stereo = ((Machine.drv.sound_attributes & SOUND_SUPPORTS_STEREO) != 0);
    
        /* clear the accumulators */
        accum_base = 0;
        for (int i = 0; i < ACCUMULATOR_SAMPLES; i++)
        {
            left_accum[i] = 0;
            right_accum[i] = 0;
        }
    
    	samples_this_frame = osd_start_audio_stream(is_stereo);
    
    	mixer_sound_enabled = 1;
 /*temphack*/       config_invalid=0;  //we don't support read config so place that here... TODO be removed!!
    	return 0;
    }
    
    
    /*TODO*////***************************************************************************
    /*TODO*///	mixer_sh_stop
    /*TODO*///***************************************************************************/
    /*TODO*///
    public static void mixer_sh_stop()
    {
    /*TODO*///	osd_stop_audio_stream();
    }
    
    
    /***************************************************************************
    	mixer_update_channel
    ***************************************************************************/
    

    public static void mixer_update_channel(mixer_channel_data channel, int total_sample_count)
    {
    	int samples_to_generate = total_sample_count - channel.samples_available;
    
    	/* don't do anything for streaming channels */
    	if (channel.is_stream)
    		return;
    
    	/* if we're all caught up, just return */
    	if (samples_to_generate <= 0)
    		return;
    
    	/* if we're playing, mix in the data */
    	if (channel.is_playing)
    	{
    		if (channel.is_16bit)
                {
    			//mix_sample_16(channel, samples_to_generate);
                    throw new UnsupportedOperationException("mix sample 16bit unsupported ");
                }
    		else
    			mix_sample_8(channel, samples_to_generate);
    	}
    
    	/* just eat the rest */
    	channel.samples_available += samples_to_generate;
    }
    
    
    /***************************************************************************
    	mixer_sh_update
    ***************************************************************************/
    
    public static void mixer_sh_update()
    {
    	int/*UINT32 */accum_pos = accum_base;
    	//INT16 *mix;
    	int sample;
    	
    	/* update all channels (for streams this is a no-op) */
    	for (int i = 0; i < first_free_channel; i++)
    	{
    		mixer_update_channel(mixer_channel[i], samples_this_frame);
    
    		/* if we needed more than they could give, adjust their pointers */
    		if (samples_this_frame > mixer_channel[i].samples_available)
    			mixer_channel[i].samples_available = 0;
    		else
    			mixer_channel[i].samples_available -= samples_this_frame;
    	}
    
    	/* copy the mono 32-bit data to a 16-bit buffer, clipping along the way */
    	if (!is_stereo)
    	{
    		int mix = 0;//mix = mix_buffer;
    		for (int i = 0; i < samples_this_frame; i++)
    		{
    			/* fetch and clip the sample */
    			sample = left_accum[accum_pos];
    //#if !DISABLE_CLIPPING
    			if (sample < -32768)
    				sample = -32768;
    			else if (sample > 32767)
    				sample = 32767;
    //#endif
    
    			/* store and zero out behind us */
    			mix_buffer[mix++]=(short)sample;//*mix++ = sample;
    			left_accum[accum_pos] = 0;
    
    			/* advance to the next sample */
    			accum_pos = (accum_pos + 1) & ACCUMULATOR_MASK;
    		}
    	}
    
    	/* copy the stereo 32-bit data to a 16-bit buffer, clipping along the way */
    	else
    	{
    		int mix = 0;//mix = mix_buffer;
    		for (int i = 0; i < samples_this_frame; i++)
    		{
    			/* fetch and clip the left sample */
    			sample = left_accum[accum_pos];
    //#if !DISABLE_CLIPPING
    			if (sample < -32768)
    				sample = -32768;
    			else if (sample > 32767)
    				sample = 32767;
    //#endif
    
    			/* store and zero out behind us */
    			mix_buffer[mix++] = (short)sample;//*mix++ = sample;
    			left_accum[accum_pos] = 0;
    
    			/* fetch and clip the right sample */
    			sample = right_accum[accum_pos];
    //#if !DISABLE_CLIPPING
    			if (sample < -32768)
    				sample = -32768;
    			else if (sample > 32767)
    				sample = 32767;
    //#endif
    
    			/* store and zero out behind us */
    			mix_buffer[mix++] = (short)sample;//*mix++ = sample;
    			right_accum[accum_pos] = 0;
    
    			/* advance to the next sample */
    			accum_pos = (accum_pos + 1) & ACCUMULATOR_MASK;
    		}
    	}
    
    	/* play the result */
    	samples_this_frame = osd_update_audio_stream(mix_buffer);
    
    	accum_base = accum_pos;
    }
    
    /***************************************************************************
    	mixer_allocate_channel
    ***************************************************************************/
    
    public static int mixer_allocate_channel(int default_mixing_level)
    {
    	/* this is just a degenerate case of the multi-channel mixer allocate */
    	return mixer_allocate_channels(1, new int[] {default_mixing_level});
    }
    
    
    /***************************************************************************
    	mixer_allocate_channels
    ***************************************************************************/
    
    public static int mixer_allocate_channels(int channels,int[] default_mixing_levels)
    {
    	int i, j;
    
    	/* make sure we didn't overrun the number of available channels */
    	if (first_free_channel + channels > MIXER_MAX_CHANNELS)
    	{
    		if (errorlog!=null)
    			fprintf(errorlog, "Too many mixer channels (requested %d, available %d)\n", first_free_channel + channels, MIXER_MAX_CHANNELS);
    		throw new UnsupportedOperationException("Too many mixer channels");
    	}
    
    	/* loop over channels requested */
    	for (i = 0; i < channels; i++)
    	{
    		/* extract the basic data */
    		mixer_channel[first_free_channel + i].default_mixing_level 	= (char)(MIXER_GET_LEVEL(default_mixing_levels[i])&0xFF);
    		mixer_channel[first_free_channel + i].pan 					= MIXER_GET_PAN(default_mixing_levels[i]);
    		mixer_channel[first_free_channel + i].gain 					= MIXER_GET_GAIN(default_mixing_levels[i]);
    		mixer_channel[first_free_channel + i].volume 				= 100;
    
    		/* backwards compatibility with old 0-255 volume range */
    		if (mixer_channel[first_free_channel + i].default_mixing_level > 100)
    			mixer_channel[first_free_channel + i].default_mixing_level = (char)((mixer_channel[first_free_channel + i].default_mixing_level * 25 / 255)&0xFF);
    
    		/* attempt to load in the configuration data for this channel */
    		mixer_channel[first_free_channel + i].mixing_level = mixer_channel[first_free_channel + i].default_mixing_level;
    		if (config_invalid==0)
    		{
    			/* if the defaults match, set the mixing level from the config */
    			if (mixer_channel[first_free_channel + i].default_mixing_level == mixer_channel[first_free_channel + i].config_default_mixing_level)
    				mixer_channel[first_free_channel + i].mixing_level = mixer_channel[first_free_channel + i].config_mixing_level;
    
    			/* otherwise, invalidate all channels that have been created so far */
    			else
    			{
    				config_invalid = 1;
    				for (j = 0; j < first_free_channel + i; j++)
    					mixer_set_mixing_level(j, mixer_channel[j].default_mixing_level);
    			}
    		}
    
    		/* set the default name */
    		mixer_set_name(first_free_channel + i, null);
    	}
    
    	/* increment the counter and return the first one */
    	first_free_channel += channels;
    	return first_free_channel - channels;
    }
    
    
    /***************************************************************************
    	mixer_set_name
    ***************************************************************************/
    
    public static void mixer_set_name(int ch, String name)
    {
    	/* either copy the name or create a default one */
        if (name != null)
                mixer_channel[ch].name = name;
            else
                mixer_channel[ch].name = sprintf("<channel #%d>", ch);
  
    	/* append left/right onto the channel as appropriate */
        if (mixer_channel[ch].pan == MIXER_PAN_LEFT)
                mixer_channel[ch].name += " (Lt)";
            else if (mixer_channel[ch].pan == MIXER_PAN_RIGHT)
                mixer_channel[ch].name += " (Rt)";
    }
    
    
    /***************************************************************************
    /*TODO*///	mixer_get_name
    /*TODO*///***************************************************************************/
    /*TODO*///
    /*TODO*///const char *mixer_get_name(int ch)
    /*TODO*///{
    /*TODO*///	struct mixer_channel_data *channel = &mixer_channel[ch];
    /*TODO*///
    /*TODO*///	/* return a pointer to the name or a NULL for an unused channel */
    /*TODO*///	if (channel->name[0] != 0)
    /*TODO*///		return channel->name;
    /*TODO*///	else
    /*TODO*///		return NULL;
    /*TODO*///}
    /*TODO*///
    /*TODO*///
    /*TODO*////***************************************************************************
    /*TODO*///	mixer_set_volume
    /*TODO*///***************************************************************************/
    /*TODO*///
    /*TODO*///void mixer_set_volume(int ch, int volume)
    /*TODO*///{
    /*TODO*///	struct mixer_channel_data *channel = &mixer_channel[ch];
    /*TODO*///
    /*TODO*///	mixer_update_channel(channel, sound_scalebufferpos(samples_this_frame));
    /*TODO*///	channel->volume = volume;
    /*TODO*///}
    /*TODO*///
    
    /***************************************************************************
    	mixer_set_mixing_level
    ***************************************************************************/
    
    public static void mixer_set_mixing_level(int ch, int level)
    {

    	mixer_update_channel(mixer_channel[ch], sound_scalebufferpos(samples_this_frame));
    	mixer_channel[ch].mixing_level = (char)(level&0xFF);
    }
    
    /*TODO*///
    /*TODO*////***************************************************************************
    /*TODO*///	mixer_get_mixing_level
    /*TODO*///***************************************************************************/
    /*TODO*///
    /*TODO*///int mixer_get_mixing_level(int ch)
    /*TODO*///{
    /*TODO*///	struct mixer_channel_data *channel = &mixer_channel[ch];
    /*TODO*///	return channel->mixing_level;
    /*TODO*///}
    /*TODO*///
    /*TODO*///
    /*TODO*////***************************************************************************
    /*TODO*///	mixer_get_default_mixing_level
    /*TODO*///***************************************************************************/
    /*TODO*///
    /*TODO*///int mixer_get_default_mixing_level(int ch)
    /*TODO*///{
    /*TODO*///	struct mixer_channel_data *channel = &mixer_channel[ch];
    /*TODO*///	return channel->default_mixing_level;
    /*TODO*///}
    /*TODO*///
    /*TODO*///
    /*TODO*////***************************************************************************
    /*TODO*///	mixer_read_config
    /*TODO*///***************************************************************************/
    /*TODO*///
    /*TODO*///void mixer_read_config(void *f)
    /*TODO*///{
    /*TODO*///	UINT8 default_levels[MIXER_MAX_CHANNELS];
    /*TODO*///	UINT8 mixing_levels[MIXER_MAX_CHANNELS];
    /*TODO*///	int i;
    /*TODO*///
    /*TODO*///	memset(default_levels, 0xff, sizeof(default_levels));
    /*TODO*///	memset(mixing_levels, 0xff, sizeof(mixing_levels));
    /*TODO*///	osd_fread(f, default_levels, MIXER_MAX_CHANNELS);
    /*TODO*///	osd_fread(f, mixing_levels, MIXER_MAX_CHANNELS);
    /*TODO*///	for (i = 0; i < MIXER_MAX_CHANNELS; i++)
    /*TODO*///	{
    /*TODO*///		config_default_mixing_level[i] = default_levels[i];
    /*TODO*///		config_mixing_level[i] = mixing_levels[i];
    /*TODO*///	}
    /*TODO*///	config_invalid = 0;
    /*TODO*///}
    /*TODO*///
    /*TODO*///
    /*TODO*////***************************************************************************
    /*TODO*///	mixer_write_config
    /*TODO*///***************************************************************************/
    /*TODO*///
    /*TODO*///void mixer_write_config(void *f)
    /*TODO*///{
    /*TODO*///	UINT8 default_levels[MIXER_MAX_CHANNELS];
    /*TODO*///	UINT8 mixing_levels[MIXER_MAX_CHANNELS];
    /*TODO*///	int i;
    /*TODO*///
    /*TODO*///	for (i = 0; i < MIXER_MAX_CHANNELS; i++)
    /*TODO*///	{
    /*TODO*///		default_levels[i] = mixer_channel[i].default_mixing_level;
    /*TODO*///		mixing_levels[i] = mixer_channel[i].mixing_level;
    /*TODO*///	}
    /*TODO*///	osd_fwrite(f, default_levels, MIXER_MAX_CHANNELS);
    /*TODO*///	osd_fwrite(f, mixing_levels, MIXER_MAX_CHANNELS);
    /*TODO*///}
    /*TODO*///
    /*TODO*///
    /*TODO*////***************************************************************************
    /*TODO*///	mixer_play_streamed_sample_16
    /*TODO*///***************************************************************************/
    /*TODO*///
    public static void mixer_play_streamed_sample_16(int ch, CharPtr data, int len, int freq)
    {
    	int/*UINT32*/ step_size, input_pos, output_pos, samples_mixed;
        int mixing_volume;
    
    	/* skip if sound is off */
    	if (Machine.sample_rate == 0)
    		return;
    	mixer_channel[ch].is_stream = true;
    
    	/* compute the overall mixing volume */
    	if (mixer_sound_enabled!=0)
    		mixing_volume = ((mixer_channel[ch].volume * mixer_channel[ch].mixing_level * 256) << mixer_channel[ch].gain) / (100*100);
    	else
    		mixing_volume = 0;
    
    	/* compute the step size for sample rate conversion */
    	if (freq != mixer_channel[ch].frequency)
    	{
    		mixer_channel[ch].frequency = freq;
    		mixer_channel[ch].step_size = (int/*UINT32*/)((double)freq * (double)(1 << FRACTION_BITS) / (double)Machine.sample_rate);
                if(mixer_channel[ch].step_size<0)//do a check (to be remove) (shadow)
                {
                    throw new UnsupportedOperationException("step_size <1");
                }
        }
    	step_size = mixer_channel[ch].step_size;
    
    	/* now determine where to mix it */
    	input_pos = mixer_channel[ch].input_frac;
    	output_pos = (accum_base + mixer_channel[ch].samples_available) & ACCUMULATOR_MASK;
    
    	/* compute the length in fractional form */
    	len = (len / 2) << FRACTION_BITS;
    	samples_mixed = 0;
    
    	/* if we're mono or left panning, just mix to the left channel */
    	if (!is_stereo || mixer_channel[ch].pan == MIXER_PAN_LEFT)
    	{
    		while (input_pos < len)
    		{
    			left_accum[output_pos] += (data.read(input_pos >> FRACTION_BITS) * mixing_volume) >> 8;
    			input_pos += step_size;
    			output_pos = (output_pos + 1) & ACCUMULATOR_MASK;
    			samples_mixed++;
    		}
    	}
    
    	/* if we're right panning, just mix to the right channel */
    	else if (mixer_channel[ch].pan == MIXER_PAN_RIGHT)
    	{
    		while (input_pos < len)
    		{
    			right_accum[output_pos] += (data.read(input_pos >> FRACTION_BITS) * mixing_volume) >> 8;
    			input_pos += step_size;
    			output_pos = (output_pos + 1) & ACCUMULATOR_MASK;
    			samples_mixed++;
    		}
    	}
    
    	/* if we're stereo center, mix to both channels */
    	else
    	{
    		while (input_pos < len)
    		{
    			int mixing_value = (data.read(input_pos >> FRACTION_BITS) * mixing_volume) >> 8;
    			left_accum[output_pos] += mixing_value;
    			right_accum[output_pos] += mixing_value;
    			input_pos += step_size;
    			output_pos = (output_pos + 1) & ACCUMULATOR_MASK;
    			samples_mixed++;
    		}
    	}
    
    	/* update the final positions */
    	mixer_channel[ch].input_frac = input_pos & FRACTION_MASK;
    	mixer_channel[ch].samples_available += samples_mixed;
    
    }
    /*TODO*///
    /*TODO*///
    /*TODO*////***************************************************************************
    /*TODO*///	mixer_samples_this_frame
    /*TODO*///***************************************************************************/
    /*TODO*///
    /*TODO*///int mixer_samples_this_frame(void)
    /*TODO*///{
    /*TODO*///	return samples_this_frame;
    /*TODO*///}
    /*TODO*///
    
    /***************************************************************************
    	mixer_need_samples_this_frame
    ***************************************************************************/
    public static final int EXTRA_SAMPLES =1;    // safety margin for sampling rate conversion
    public static int mixer_need_samples_this_frame(int channel,int freq)
    {
    	return (samples_this_frame - mixer_channel[channel].samples_available + EXTRA_SAMPLES)
    			* freq / Machine.sample_rate;
    }
    
    /*TODO*///
    /*TODO*////***************************************************************************
    /*TODO*///	mixer_play_sample
    /*TODO*///***************************************************************************/
    /*TODO*///
    /*TODO*///void mixer_play_sample(int ch, INT8 *data, int len, int freq, int loop)
    /*TODO*///{
    /*TODO*///	struct mixer_channel_data *channel = &mixer_channel[ch];
    /*TODO*///
    /*TODO*///	/* skip if sound is off, or if this channel is a stream */
    /*TODO*///	if (Machine->sample_rate == 0 || channel->is_stream)
    /*TODO*///		return;
    /*TODO*///
    /*TODO*///	/* update the state of this channel */
    /*TODO*///	mixer_update_channel(channel, sound_scalebufferpos(samples_this_frame));
    /*TODO*///
    /*TODO*///	/* compute the step size for sample rate conversion */
    /*TODO*///	if (freq != channel->frequency)
    /*TODO*///	{
    /*TODO*///		channel->frequency = freq;
    /*TODO*///		channel->step_size = (UINT32)((double)freq * (double)(1 << FRACTION_BITS) / (double)Machine->sample_rate);
    /*TODO*///	}
    /*TODO*///
    /*TODO*///	/* now determine where to mix it */
    /*TODO*///	channel->input_frac = 0;
    /*TODO*///	channel->data_start = data;
    /*TODO*///	channel->data_current = data;
    /*TODO*///	channel->data_end = (UINT8 *)data + len;
    /*TODO*///	channel->is_playing = 1;
    /*TODO*///	channel->is_looping = loop;
    /*TODO*///	channel->is_16bit = 0;
    /*TODO*///}
    /*TODO*///
    /*TODO*///
    /*TODO*////***************************************************************************
    /*TODO*///	mixer_play_sample_16
    /*TODO*///***************************************************************************/
    /*TODO*///
    /*TODO*///void mixer_play_sample_16(int ch, INT16 *data, int len, int freq, int loop)
    /*TODO*///{
    /*TODO*///	struct mixer_channel_data *channel = &mixer_channel[ch];
    /*TODO*///
    /*TODO*///	/* skip if sound is off, or if this channel is a stream */
    /*TODO*///	if (Machine->sample_rate == 0 || channel->is_stream)
    /*TODO*///		return;
    /*TODO*///
    /*TODO*///	/* update the state of this channel */
    /*TODO*///	mixer_update_channel(channel, sound_scalebufferpos(samples_this_frame));
    /*TODO*///
    /*TODO*///	/* compute the step size for sample rate conversion */
    /*TODO*///	if (freq != channel->frequency)
    /*TODO*///	{
    /*TODO*///		channel->frequency = freq;
    /*TODO*///		channel->step_size = (UINT32)((double)freq * (double)(1 << FRACTION_BITS) / (double)Machine->sample_rate);
    /*TODO*///	}
    /*TODO*///
    /*TODO*///	/* now determine where to mix it */
    /*TODO*///	channel->input_frac = 0;
    /*TODO*///	channel->data_start = data;
    /*TODO*///	channel->data_current = data;
    /*TODO*///	channel->data_end = (UINT8 *)data + len;
    /*TODO*///	channel->is_playing = 1;
    /*TODO*///	channel->is_looping = loop;
    /*TODO*///	channel->is_16bit = 1;
    /*TODO*///}
    /*TODO*///
    /*TODO*///
    /*TODO*////***************************************************************************
    /*TODO*///	mixer_stop_sample
    /*TODO*///***************************************************************************/
    /*TODO*///
    /*TODO*///void mixer_stop_sample(int ch)
    /*TODO*///{
    /*TODO*///	struct mixer_channel_data *channel = &mixer_channel[ch];
    /*TODO*///
    /*TODO*///	mixer_update_channel(channel, sound_scalebufferpos(samples_this_frame));
    /*TODO*///	channel->is_playing = 0;
    /*TODO*///}
    /*TODO*///
    /*TODO*///
    /*TODO*////***************************************************************************
    /*TODO*///	mixer_is_sample_playing
    /*TODO*///***************************************************************************/
    /*TODO*///
    /*TODO*///int mixer_is_sample_playing(int ch)
    /*TODO*///{
    /*TODO*///	struct mixer_channel_data *channel = &mixer_channel[ch];
    /*TODO*///
    /*TODO*///	mixer_update_channel(channel, sound_scalebufferpos(samples_this_frame));
    /*TODO*///	return channel->is_playing;
    /*TODO*///}
    /*TODO*///
    /*TODO*///
    /*TODO*////***************************************************************************
    /*TODO*///	mixer_set_sample_frequency
    /*TODO*///***************************************************************************/
    /*TODO*///
    /*TODO*///void mixer_set_sample_frequency(int ch, int freq)
    /*TODO*///{
    /*TODO*///	struct mixer_channel_data *channel = &mixer_channel[ch];
    /*TODO*///
    /*TODO*///	mixer_update_channel(channel, sound_scalebufferpos(samples_this_frame));
    /*TODO*///
    /*TODO*///	/* compute the step size for sample rate conversion */
    /*TODO*///	if (freq != channel->frequency)
    /*TODO*///	{
    /*TODO*///		channel->frequency = freq;
    /*TODO*///		channel->step_size = (UINT32)((double)freq * (double)(1 << FRACTION_BITS) / (double)Machine->sample_rate);
    /*TODO*///	}
    /*TODO*///}
    /*TODO*///
    /*TODO*///
    /*TODO*////***************************************************************************
    /*TODO*///	mixer_sound_enable_global_w
    /*TODO*///***************************************************************************/
    /*TODO*///
    /*TODO*///void mixer_sound_enable_global_w(int enable)
    /*TODO*///{
    /*TODO*///	int i;
    /*TODO*///	struct mixer_channel_data *channel;
    /*TODO*///
    /*TODO*///	/* update all channels (for streams this is a no-op) */
    /*TODO*///	for (i = 0, channel = mixer_channel; i < first_free_channel; i++, channel++)
    /*TODO*///	{
    /*TODO*///		mixer_update_channel(channel, sound_scalebufferpos(samples_this_frame));
    /*TODO*///	}
    /*TODO*///
    /*TODO*///	mixer_sound_enabled = enable;
    /*TODO*///}
    /*TODO*///
    
    /***************************************************************************
    	mix_sample_8
    ***************************************************************************/
    
    public static void mix_sample_8(mixer_channel_data channel, int samples_to_generate)
    {
    	int/*UINT32*/ step_size, input_frac, output_pos;
    	//INT8 *source, *source_end;
        CharPtr source;
        int source_end;
    	int mixing_volume;
    
    	/* compute the overall mixing volume */
    	if (mixer_sound_enabled!=0)
    		mixing_volume = ((channel.volume * channel.mixing_level * 256) << channel.gain) / (100*100);
    	else
    		mixing_volume = 0;
    
    	/* get the initial state */
    	step_size = channel.step_size;
    	source = new CharPtr(channel.data_start,channel.data_current);
    	source_end = channel.data_end;
    	input_frac = channel.input_frac;
    	output_pos = (accum_base + channel.samples_available) & ACCUMULATOR_MASK;
    
    	/* an outer loop to handle looping samples */
    	while (samples_to_generate > 0)
    	{
    		/* if we're mono or left panning, just mix to the left channel */
    		if (!is_stereo || channel.pan == MIXER_PAN_LEFT)
    		{
    			while (source.base < source_end && samples_to_generate > 0)
    			{
    				left_accum[output_pos] += (source.read()&0xff) * mixing_volume;
    				input_frac += step_size;
    				source.base += (int)(input_frac >> FRACTION_BITS);
    				input_frac &= FRACTION_MASK;
    				output_pos = (output_pos + 1) & ACCUMULATOR_MASK;
    				samples_to_generate--;
    			}
    		}
    
    		/* if we're right panning, just mix to the right channel */
    		else if (channel.pan == MIXER_PAN_RIGHT)
    		{
    			while (source.base < source_end && samples_to_generate > 0)
    			{
    				right_accum[output_pos] += (source.read()&0xff) * mixing_volume;
    				input_frac += step_size;
    				source.base += (int)(input_frac >> FRACTION_BITS);
    				input_frac &= FRACTION_MASK;
    				output_pos = (output_pos + 1) & ACCUMULATOR_MASK;
    				samples_to_generate--;
    			}
    		}
    
    		/* if we're stereo center, mix to both channels */
    		else
    		{
    			while (source.base < source_end && samples_to_generate > 0)
    			{
    				int mixing_value = (source.read()&0xff) * mixing_volume;
    				left_accum[output_pos] += mixing_value;
    				right_accum[output_pos] += mixing_value;
    				input_frac += step_size;
    				source.base += (int)(input_frac >> FRACTION_BITS);
    				input_frac &= FRACTION_MASK;
    				output_pos = (output_pos + 1) & ACCUMULATOR_MASK;
    				samples_to_generate--;
    			}
    		}
    
    		/* handle the end case */
    		if (source.base >= source_end)
    		{
    			/* if we're done, stop playing */
    			if (!channel.is_looping)
    			{
    				channel.is_playing = false;
    				break;
    			}
    
    			/* if we're looping, wrap to the beginning */
    			else
    				 source.base -= source_end;//source -= (INT8 *)source_end - (INT8 *)channel.data_start;
    		}
    	}
    
    	/* update the final positions */
    	channel.input_frac = input_frac;
    	channel.data_current = source.base;
    }
    
    
    /*TODO*////***************************************************************************
    /*TODO*///	mix_sample_16
    /*TODO*///***************************************************************************/
    /*TODO*///
    /*TODO*///void mix_sample_16(struct mixer_channel_data *channel, int samples_to_generate)
    /*TODO*///{
    /*TODO*///	UINT32 step_size, input_frac, output_pos;
    /*TODO*///	INT16 *source, *source_end;
    /*TODO*///	INT32 mixing_volume;
    /*TODO*///
    /*TODO*///	/* compute the overall mixing volume */
    /*TODO*///	if (mixer_sound_enabled)
    /*TODO*///		mixing_volume = ((channel->volume * channel->mixing_level * 256) << channel->gain) / (100*100);
    /*TODO*///	else
    /*TODO*///		mixing_volume = 0;
    /*TODO*///
    /*TODO*///	/* get the initial state */
    /*TODO*///	step_size = channel->step_size;
    /*TODO*///	source = channel->data_current;
    /*TODO*///	source_end = channel->data_end;
    /*TODO*///	input_frac = channel->input_frac;
    /*TODO*///	output_pos = (accum_base + channel->samples_available) & ACCUMULATOR_MASK;
    /*TODO*///
    /*TODO*///	/* an outer loop to handle looping samples */
    /*TODO*///	while (samples_to_generate > 0)
    /*TODO*///	{
    /*TODO*///		/* if we're mono or left panning, just mix to the left channel */
    /*TODO*///		if (!is_stereo || channel->pan == MIXER_PAN_LEFT)
    /*TODO*///		{
    /*TODO*///			while (source < source_end && samples_to_generate > 0)
    /*TODO*///			{
    /*TODO*///				left_accum[output_pos] += (*source * mixing_volume) >> 8;
    /*TODO*///
    /*TODO*///				input_frac += step_size;
    /*TODO*///				source += input_frac >> FRACTION_BITS;
    /*TODO*///				input_frac &= FRACTION_MASK;
    /*TODO*///
    /*TODO*///				output_pos = (output_pos + 1) & ACCUMULATOR_MASK;
    /*TODO*///				samples_to_generate--;
    /*TODO*///			}
    /*TODO*///		}
    /*TODO*///
    /*TODO*///		/* if we're right panning, just mix to the right channel */
    /*TODO*///		else if (channel->pan == MIXER_PAN_RIGHT)
    /*TODO*///		{
    /*TODO*///			while (source < source_end && samples_to_generate > 0)
    /*TODO*///			{
    /*TODO*///				right_accum[output_pos] += (*source * mixing_volume) >> 8;
    /*TODO*///
    /*TODO*///				input_frac += step_size;
    /*TODO*///				source += input_frac >> FRACTION_BITS;
    /*TODO*///				input_frac &= FRACTION_MASK;
    /*TODO*///
    /*TODO*///				output_pos = (output_pos + 1) & ACCUMULATOR_MASK;
    /*TODO*///				samples_to_generate--;
    /*TODO*///			}
    /*TODO*///		}
    /*TODO*///
    /*TODO*///		/* if we're stereo center, mix to both channels */
    /*TODO*///		else
    /*TODO*///		{
    /*TODO*///			while (source < source_end && samples_to_generate > 0)
    /*TODO*///			{
    /*TODO*///				INT32 mixing_value = (*source * mixing_volume) >> 8;
    /*TODO*///				left_accum[output_pos] += mixing_value;
    /*TODO*///				right_accum[output_pos] += mixing_value;
    /*TODO*///
    /*TODO*///				input_frac += step_size;
    /*TODO*///				source += input_frac >> FRACTION_BITS;
    /*TODO*///				input_frac &= FRACTION_MASK;
    /*TODO*///
    /*TODO*///				output_pos = (output_pos + 1) & ACCUMULATOR_MASK;
    /*TODO*///				samples_to_generate--;
    /*TODO*///			}
    /*TODO*///		}
    /*TODO*///
    /*TODO*///		/* handle the end case */
    /*TODO*///		if (source >= source_end)
    /*TODO*///		{
    /*TODO*///			/* if we're done, stop playing */
    /*TODO*///			if (!channel->is_looping)
    /*TODO*///			{
    /*TODO*///				channel->is_playing = 0;
    /*TODO*///				break;
    /*TODO*///			}
    /*TODO*///
    /*TODO*///			/* if we're looping, wrap to the beginning */
    /*TODO*///			else
    /*TODO*///				source -= (INT16 *)source_end - (INT16 *)channel->data_start;
    /*TODO*///		}
    /*TODO*///	}
    /*TODO*///
    /*TODO*///	/* update the final positions */
    /*TODO*///	channel->input_frac = input_frac;
    /*TODO*///	channel->data_current = source;
    /*TODO*///}
    /*TODO*///
    /*TODO*///
    /*TODO*///    
}