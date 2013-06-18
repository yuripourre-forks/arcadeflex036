
package mame;

import static mame.driverH.*;
import static mame.mame.*;
import static mame.drawgfxH.*;
import static mame.drawgfx.*;
import static arcadeflex.libc.*;
import static arcadeflex.libc_old.*;
import static arcadeflex.video.*;
import static mame.usrintrfH.*;
import static mame.inputH.*;

public class usrintrf {
    /*TODO*///#define SEL_BITS 12
    /*TODO*///#define SEL_MASK ((1<<SEL_BITS)-1)
    /*TODO*///
    /*TODO*///extern int mame_debug;
    /*TODO*///
    /*TODO*///extern int need_to_clear_bitmap;	/* used to tell updatescreen() to clear the bitmap */
    /*TODO*///extern int bitmap_dirty;	/* set by osd_clearbitmap() */
    /*TODO*///
    /*TODO*////* Variables for stat menu */
    /*TODO*///extern char build_version[];
    /*TODO*///extern unsigned int dispensed_tickets;
    /*TODO*///extern unsigned int coins[COIN_COUNTERS];
    /*TODO*///extern unsigned int coinlockedout[COIN_COUNTERS];
    /*TODO*///
    /*TODO*////* MARTINEZ.F 990207 Memory Card */
    /*TODO*///#ifndef NEOFREE
    /*TODO*///#ifndef TINY_COMPILE
    /*TODO*///int 		memcard_menu(int);
    /*TODO*///extern int	mcd_action;
    /*TODO*///extern int	mcd_number;
    /*TODO*///extern int	memcard_status;
    /*TODO*///extern int	memcard_number;
    /*TODO*///extern int	memcard_manager;
    /*TODO*///#endif
    /*TODO*///#endif
    /*TODO*///
    /*TODO*///extern int neogeo_memcard_load(int);
    /*TODO*///extern void neogeo_memcard_save(void);
    /*TODO*///extern void neogeo_memcard_eject(void);
    /*TODO*///extern int neogeo_memcard_create(int);
    /*TODO*////* MARTINEZ.F 990207 Memory Card End */
    /*TODO*///
    /*TODO*///
    /*TODO*///
    public static int setup_selected;
    /*TODO*///static int osd_selected;
    /*TODO*///static int jukebox_selected;
    /*TODO*///static int single_step;
    /*TODO*///
    /*TODO*///
    /*TODO*///
   public static void set_ui_visarea (int xmin, int ymin, int xmax, int ymax)
   {
   	int temp,w,h;
   
   	/* special case for vectors */
   	if(Machine.drv.video_attributes == VIDEO_TYPE_VECTOR)
   	{
   		if ((Machine.ui_orientation & ORIENTATION_SWAP_XY)!=0)
   		{
   			temp=xmin; xmin=ymin; ymin=temp;
   			temp=xmax; xmax=ymax; ymax=temp;
   		}
   	}
   	else
   	{
   		if ((Machine.orientation & ORIENTATION_SWAP_XY)!=0)
   		{
   			w = Machine.drv.screen_height;
   			h = Machine.drv.screen_width;
   		}
   		else
   		{
   			w = Machine.drv.screen_width;
   			h = Machine.drv.screen_height;
   		}
   
   		if ((Machine.ui_orientation & ORIENTATION_FLIP_X)!=0)
   		{
   			temp = w - xmin - 1;
   			xmin = w - xmax - 1;
   			xmax = temp ;
   		}
   
   		if ((Machine.ui_orientation & ORIENTATION_FLIP_Y)!=0)
   		{
   			temp = h - ymin - 1;
   			ymin = h - ymax - 1;
   			ymax = temp;
   		}
   
   		if ((Machine.ui_orientation & ORIENTATION_SWAP_XY)!=0)
   		{
   			temp = xmin; xmin = ymin; ymin = temp;
   			temp = xmax; xmax = ymax; ymax = temp;
   		}
   
   	}
   	Machine.uiwidth = xmax-xmin+1;
   	Machine.uiheight = ymax-ymin+1;
   	Machine.uixmin = xmin;
   	Machine.uiymin = ymin;
   }
   
    public static GfxElement builduifont()
    {
    	char fontdata6x8[] =
    	{
    		0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
    		0x7c,0x80,0x98,0x90,0x80,0xbc,0x80,0x7c,0xf8,0x04,0x64,0x44,0x04,0xf4,0x04,0xf8,
    		0x7c,0x80,0x98,0x88,0x80,0xbc,0x80,0x7c,0xf8,0x04,0x64,0x24,0x04,0xf4,0x04,0xf8,
    		0x7c,0x80,0x88,0x98,0x80,0xbc,0x80,0x7c,0xf8,0x04,0x24,0x64,0x04,0xf4,0x04,0xf8,
    		0x7c,0x80,0x90,0x98,0x80,0xbc,0x80,0x7c,0xf8,0x04,0x44,0x64,0x04,0xf4,0x04,0xf8,
    		0x30,0x48,0x84,0xb4,0xb4,0x84,0x48,0x30,0x30,0x48,0x84,0x84,0x84,0x84,0x48,0x30,
    		0x00,0xfc,0x84,0x8c,0xd4,0xa4,0xfc,0x00,0x00,0xfc,0x84,0x84,0x84,0x84,0xfc,0x00,
    		0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x30,0x68,0x78,0x78,0x30,0x00,0x00,
    		0x80,0xc0,0xe0,0xf0,0xe0,0xc0,0x80,0x00,0x04,0x0c,0x1c,0x3c,0x1c,0x0c,0x04,0x00,
    		0x20,0x70,0xf8,0x20,0x20,0xf8,0x70,0x20,0x48,0x48,0x48,0x48,0x48,0x00,0x48,0x00,
    		0x00,0x00,0x30,0x68,0x78,0x30,0x00,0x00,0x00,0x30,0x68,0x78,0x78,0x30,0x00,0x00,
    		0x70,0xd8,0xe8,0xe8,0xf8,0xf8,0x70,0x00,0x1c,0x7c,0x74,0x44,0x44,0x4c,0xcc,0xc0,
    		0x20,0x70,0xf8,0x70,0x70,0x70,0x70,0x00,0x70,0x70,0x70,0x70,0xf8,0x70,0x20,0x00,
    		0x00,0x10,0xf8,0xfc,0xf8,0x10,0x00,0x00,0x00,0x20,0x7c,0xfc,0x7c,0x20,0x00,0x00,
    		0xb0,0x54,0xb8,0xb8,0x54,0xb0,0x00,0x00,0x00,0x28,0x6c,0xfc,0x6c,0x28,0x00,0x00,
    		0x00,0x30,0x30,0x78,0x78,0xfc,0x00,0x00,0xfc,0x78,0x78,0x30,0x30,0x00,0x00,0x00,
    		0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x20,0x20,0x20,0x20,0x20,0x00,0x20,0x00,
    		0x50,0x50,0x50,0x00,0x00,0x00,0x00,0x00,0x00,0x50,0xf8,0x50,0xf8,0x50,0x00,0x00,
    		0x20,0x70,0xc0,0x70,0x18,0xf0,0x20,0x00,0x40,0xa4,0x48,0x10,0x20,0x48,0x94,0x08,
    		0x60,0x90,0xa0,0x40,0xa8,0x90,0x68,0x00,0x10,0x20,0x40,0x00,0x00,0x00,0x00,0x00,
    		0x20,0x40,0x40,0x40,0x40,0x40,0x20,0x00,0x10,0x08,0x08,0x08,0x08,0x08,0x10,0x00,
    		0x20,0xa8,0x70,0xf8,0x70,0xa8,0x20,0x00,0x00,0x20,0x20,0xf8,0x20,0x20,0x00,0x00,
    		0x00,0x00,0x00,0x00,0x00,0x30,0x30,0x60,0x00,0x00,0x00,0xf8,0x00,0x00,0x00,0x00,
    		0x00,0x00,0x00,0x00,0x00,0x30,0x30,0x00,0x00,0x08,0x10,0x20,0x40,0x80,0x00,0x00,
    		0x70,0x88,0x88,0x88,0x88,0x88,0x70,0x00,0x10,0x30,0x10,0x10,0x10,0x10,0x10,0x00,
    		0x70,0x88,0x08,0x10,0x20,0x40,0xf8,0x00,0x70,0x88,0x08,0x30,0x08,0x88,0x70,0x00,
    		0x10,0x30,0x50,0x90,0xf8,0x10,0x10,0x00,0xf8,0x80,0xf0,0x08,0x08,0x88,0x70,0x00,
    		0x70,0x80,0xf0,0x88,0x88,0x88,0x70,0x00,0xf8,0x08,0x08,0x10,0x20,0x20,0x20,0x00,
    		0x70,0x88,0x88,0x70,0x88,0x88,0x70,0x00,0x70,0x88,0x88,0x88,0x78,0x08,0x70,0x00,
    		0x00,0x00,0x30,0x30,0x00,0x30,0x30,0x00,0x00,0x00,0x30,0x30,0x00,0x30,0x30,0x60,
    		0x10,0x20,0x40,0x80,0x40,0x20,0x10,0x00,0x00,0x00,0xf8,0x00,0xf8,0x00,0x00,0x00,
    		0x40,0x20,0x10,0x08,0x10,0x20,0x40,0x00,0x70,0x88,0x08,0x10,0x20,0x00,0x20,0x00,
    		0x30,0x48,0x94,0xa4,0xa4,0x94,0x48,0x30,0x70,0x88,0x88,0xf8,0x88,0x88,0x88,0x00,
    		0xf0,0x88,0x88,0xf0,0x88,0x88,0xf0,0x00,0x70,0x88,0x80,0x80,0x80,0x88,0x70,0x00,
    		0xf0,0x88,0x88,0x88,0x88,0x88,0xf0,0x00,0xf8,0x80,0x80,0xf0,0x80,0x80,0xf8,0x00,
    		0xf8,0x80,0x80,0xf0,0x80,0x80,0x80,0x00,0x70,0x88,0x80,0x98,0x88,0x88,0x70,0x00,
    		0x88,0x88,0x88,0xf8,0x88,0x88,0x88,0x00,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x00,
    		0x08,0x08,0x08,0x08,0x88,0x88,0x70,0x00,0x88,0x90,0xa0,0xc0,0xa0,0x90,0x88,0x00,
    		0x80,0x80,0x80,0x80,0x80,0x80,0xf8,0x00,0x88,0xd8,0xa8,0x88,0x88,0x88,0x88,0x00,
    		0x88,0xc8,0xa8,0x98,0x88,0x88,0x88,0x00,0x70,0x88,0x88,0x88,0x88,0x88,0x70,0x00,
    		0xf0,0x88,0x88,0xf0,0x80,0x80,0x80,0x00,0x70,0x88,0x88,0x88,0x88,0x88,0x70,0x08,
    		0xf0,0x88,0x88,0xf0,0x88,0x88,0x88,0x00,0x70,0x88,0x80,0x70,0x08,0x88,0x70,0x00,
    		0xf8,0x20,0x20,0x20,0x20,0x20,0x20,0x00,0x88,0x88,0x88,0x88,0x88,0x88,0x70,0x00,
    		0x88,0x88,0x88,0x88,0x88,0x50,0x20,0x00,0x88,0x88,0x88,0x88,0xa8,0xd8,0x88,0x00,
    		0x88,0x50,0x20,0x20,0x20,0x50,0x88,0x00,0x88,0x88,0x88,0x50,0x20,0x20,0x20,0x00,
    		0xf8,0x08,0x10,0x20,0x40,0x80,0xf8,0x00,0x30,0x20,0x20,0x20,0x20,0x20,0x30,0x00,
    		0x40,0x40,0x20,0x20,0x10,0x10,0x08,0x08,0x30,0x10,0x10,0x10,0x10,0x10,0x30,0x00,
    		0x20,0x50,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0xfc,
    		0x40,0x20,0x10,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x70,0x08,0x78,0x88,0x78,0x00,
    		0x80,0x80,0xf0,0x88,0x88,0x88,0xf0,0x00,0x00,0x00,0x70,0x88,0x80,0x80,0x78,0x00,
    		0x08,0x08,0x78,0x88,0x88,0x88,0x78,0x00,0x00,0x00,0x70,0x88,0xf8,0x80,0x78,0x00,
    		0x18,0x20,0x70,0x20,0x20,0x20,0x20,0x00,0x00,0x00,0x78,0x88,0x88,0x78,0x08,0x70,
    		0x80,0x80,0xf0,0x88,0x88,0x88,0x88,0x00,0x20,0x00,0x20,0x20,0x20,0x20,0x20,0x00,
    		0x20,0x00,0x20,0x20,0x20,0x20,0x20,0xc0,0x80,0x80,0x90,0xa0,0xe0,0x90,0x88,0x00,
    		0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x00,0x00,0x00,0xf0,0xa8,0xa8,0xa8,0xa8,0x00,
    		0x00,0x00,0xb0,0xc8,0x88,0x88,0x88,0x00,0x00,0x00,0x70,0x88,0x88,0x88,0x70,0x00,
    		0x00,0x00,0xf0,0x88,0x88,0xf0,0x80,0x80,0x00,0x00,0x78,0x88,0x88,0x78,0x08,0x08,
    		0x00,0x00,0xb0,0xc8,0x80,0x80,0x80,0x00,0x00,0x00,0x78,0x80,0x70,0x08,0xf0,0x00,
    		0x20,0x20,0x70,0x20,0x20,0x20,0x18,0x00,0x00,0x00,0x88,0x88,0x88,0x98,0x68,0x00,
    		0x00,0x00,0x88,0x88,0x88,0x50,0x20,0x00,0x00,0x00,0xa8,0xa8,0xa8,0xa8,0x50,0x00,
    		0x00,0x00,0x88,0x50,0x20,0x50,0x88,0x00,0x00,0x00,0x88,0x88,0x88,0x78,0x08,0x70,
    		0x00,0x00,0xf8,0x10,0x20,0x40,0xf8,0x00,0x08,0x10,0x10,0x20,0x10,0x10,0x08,0x00,
    		0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x40,0x20,0x20,0x10,0x20,0x20,0x40,0x00,
    		0x00,0x68,0xb0,0x00,0x00,0x00,0x00,0x00,0x20,0x50,0x20,0x50,0xa8,0x50,0x00,0x00,
    	};
    	GfxLayout fontlayout6x8 = new GfxLayout(
    		6,8,	/* 6*8 characters */
    		128,	/* 128 characters */
    		1,	/* 1 bit per pixel */
    		new int[]{ 0 },
    		new int[]{ 0, 1, 2, 3, 4, 5, 6, 7 }, /* straightforward layout */
    		new int[]{ 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
    		8*8 /* every char takes 8 consecutive bytes */
    	);
    	GfxLayout fontlayout12x8 = new GfxLayout(
    	
    		12,8,	/* 12*8 characters */
    		128,	/* 128 characters */
    		1,	/* 1 bit per pixel */
    		new int[]{ 0 },
    		new int[]{ 0,0, 1,1, 2,2, 3,3, 4,4, 5,5, 6,6, 7,7 }, /* straightforward layout */
    		new int[]{ 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
    		8*8 /* every char takes 8 consecutive bytes */
    	);
    	GfxLayout fontlayout12x16 = new GfxLayout(
    	
    		12,16,	/* 6*8 characters */
    		128,	/* 128 characters */
    		1,	/* 1 bit per pixel */
    		new int[]{ 0 },
    		new int[]{ 0,0, 1,1, 2,2, 3,3, 4,4, 5,5, 6,6, 7,7 }, /* straightforward layout */
    		new int[]{ 0*8,0*8, 1*8,1*8, 2*8,2*8, 3*8,3*8, 4*8,4*8, 5*8,5*8, 6*8,6*8, 7*8,7*8 },
    		8*8 /* every char takes 8 consecutive bytes */
    	);
    	GfxElement font;
    	char[] colortable=new char[2*2];	/* ASG 980209 */
    	int trueorientation;
    
    
    	/* hack: force the display into standard orientation to avoid */
    	/* creating a rotated font */
    	trueorientation = Machine.orientation;
    	Machine.orientation = Machine.ui_orientation;
    
    	if ((Machine.drv.video_attributes & VIDEO_PIXEL_ASPECT_RATIO_MASK)
    			== VIDEO_PIXEL_ASPECT_RATIO_1_2)
    	{
    		font = decodegfx(new UBytePtr(fontdata6x8),fontlayout12x8);
    		Machine.uifontwidth = 12;
    		Machine.uifontheight = 8;
    	}
    	else if (Machine.uiwidth >= 420 && Machine.uiheight >= 420)
    	{
    		font = decodegfx(new UBytePtr(fontdata6x8),fontlayout12x16);
    		Machine.uifontwidth = 12;
    		Machine.uifontheight = 16;
    	}
    	else
    	{
    		font = decodegfx(new UBytePtr(fontdata6x8),fontlayout6x8);
    		Machine.uifontwidth = 6;
    		Machine.uifontheight = 8;
    	}
    
    	if (font!=null)
    	{
    		/* colortable will be set at run time */
    		//memset(colortable,0,sizeof(colortable));
                font.colortable = new CharPtr(colortable);
    		font.total_colors = 2;
    	}
    
    	Machine.orientation = trueorientation;
    
    	return font;
    }
    
    
    
    /*TODO*////***************************************************************************
    /*TODO*///
    /*TODO*///  Display text on the screen. If erase is 0, it superimposes the text on
    /*TODO*///  the last frame displayed.
    /*TODO*///
    /*TODO*///***************************************************************************/
    public static void displaytext(DisplayText []dt,int erase,int update_screen)
    {
    	int trueorientation;
    
    
    	if (erase!=0)
    		osd_clearbitmap(Machine.scrbitmap);
    
    
    	/* hack: force the display into standard orientation to avoid */
    	/* rotating the user interface */
    	trueorientation = Machine.orientation;
    	Machine.orientation = Machine.ui_orientation;
    
    	osd_mark_dirty (0,0,Machine.uiwidth-1,Machine.uiheight-1,1);	/* ASG 971011 */
    
    	int _ptr = 0;
	while (dt[_ptr].text != null)
	{
    
                int x, y;
		CharBuf c = new CharBuf();


		x = dt[_ptr].x;
		y = dt[_ptr].y;
		c.set(dt[_ptr].text);
                
    		while (c.ch != 0)
    		{
    			int wrapped;
    
    
    			wrapped = 0;
    
    			if (c.ch == '\n')
    			{
    				x = dt[_ptr].x;
    				y += Machine.uifontheight + 1;
    				wrapped = 1;
    			}
    			else if (c.ch == ' ')
    			{
    				/* don't try to word wrap at the beginning of a line (this would cause */
    				/* an endless loop if a word is longer than a line) */
    				if (x != dt[_ptr].x)
    				{
    					int nextlen=0;
    					CharBuf nc = new CharBuf();
    
    
    					nc.set(c, 1);//nc = c+1;	
                                        while ((nc.ch != 0) && (nc.ch != ' ') && (nc.ch != '\n'))//while (*nc && *nc != ' ' && *nc != '\n')
    					{
    						nextlen += Machine.uifontwidth;
    						 nc.inc();
    					}
    
    					/* word wrap */
    					if (x + Machine.uifontwidth + nextlen > Machine.uiwidth)
    					{
    						x =  dt[_ptr].x;
    						y += Machine.uifontheight + 1;
    						wrapped = 1;
    					}
    				}
    			}
    
    			if (wrapped==0)
    			{
    				drawgfx(Machine.scrbitmap,Machine.uifont,c.ch, dt[_ptr].color,0,0,x+Machine.uixmin,y+Machine.uiymin,null,TRANSPARENCY_NONE,0);
    				x += Machine.uifontwidth;
    			}
    
    			c.inc();//c++;
    		}
    
    		_ptr++;//dt++
    	}
    
    	Machine.orientation = trueorientation;
    
    	if (update_screen!=0) osd_update_video_and_audio();
    }
    /*TODO*///
    /*TODO*////* Writes messages on the screen. */
    /*TODO*///static void ui_text_ex(const char* buf_begin, const char* buf_end, int x, int y, int color)
    /*TODO*///{
    /*TODO*///	int trueorientation;
    /*TODO*///
    /*TODO*///	/* hack: force the display into standard orientation to avoid */
    /*TODO*///	/* rotating the text */
    /*TODO*///	trueorientation = Machine->orientation;
    /*TODO*///	Machine->orientation = Machine->ui_orientation;
    /*TODO*///
    /*TODO*///	for (;buf_begin != buf_end; ++buf_begin)
    /*TODO*///	{
    /*TODO*///		drawgfx(Machine->scrbitmap,Machine->uifont,*buf_begin,color,0,0,
    /*TODO*///				x + Machine->uixmin,
    /*TODO*///				y + Machine->uiymin, 0,TRANSPARENCY_NONE,0);
    /*TODO*///		x += Machine->uifontwidth;
    /*TODO*///	}
    /*TODO*///
    /*TODO*///	Machine->orientation = trueorientation;
    /*TODO*///}
    /*TODO*///
    /*TODO*////* Writes messages on the screen. */
    /*TODO*///void ui_text(const char *buf,int x,int y)
    /*TODO*///{
    /*TODO*///	ui_text_ex(buf, buf + strlen(buf), x, y, DT_COLOR_WHITE);
    /*TODO*///}
    /*TODO*///
    /*TODO*///INLINE void drawpixel(int x, int y, unsigned short color)
    /*TODO*///{
    /*TODO*///	int temp;
    /*TODO*///
    /*TODO*///	if (Machine->ui_orientation & ORIENTATION_SWAP_XY)
    /*TODO*///	{
    /*TODO*///		temp = x; x = y; y = temp;
    /*TODO*///	}
    /*TODO*///	if (Machine->ui_orientation & ORIENTATION_FLIP_X)
    /*TODO*///		x = Machine->scrbitmap->width - x - 1;
    /*TODO*///	if (Machine->ui_orientation & ORIENTATION_FLIP_Y)
    /*TODO*///		y = Machine->scrbitmap->height - y - 1;
    /*TODO*///
    /*TODO*///	if (Machine->scrbitmap->depth == 16)
    /*TODO*///		*(unsigned short *)&Machine->scrbitmap->line[y][x*2] = color;
    /*TODO*///	else
    /*TODO*///		Machine->scrbitmap->line[y][x] = color;
    /*TODO*///
    /*TODO*///	osd_mark_dirty(x,y,x,y,1);
    /*TODO*///}
    /*TODO*///
    public static void drawhline_norotate(int x, int w, int y, char color)
    {
    	if (Machine.scrbitmap.depth == 16)
    	{
    		int i;
   /*TODO*///	for (i = x; i < x+w; i++)
   /*TODO*///		*(unsigned short *)&Machine->scrbitmap->line[y][i*2] = color;
                throw new UnsupportedOperationException("drawhLine 16BIT unimplemented");
    	}
    	else
    		//memset(&Machine->scrbitmap->line[y][x], color, w);
                for (int i = 0; i < w; i++)
                    Machine.scrbitmap.line[y].write(x + i,(char)color);

    
    	osd_mark_dirty(x,y,x+w-1,y,1);
    }
    
    public static void drawvline_norotate(int x, int y, int h, char color)
    {
    	int i;
    
    	if (Machine.scrbitmap.depth == 16)
    	{
  /*TODO*///  		for (i = y; i < y+h; i++)
  /*TODO*///  			*(unsigned short *)&Machine->scrbitmap->line[i][x*2] = color;
            throw new UnsupportedOperationException("drawvLine 16BIT unimplemented");
    	}
    	else
    	{
    		for (i = y; i < y+h; i++)
                   Machine.scrbitmap.line[i].write(x,(char)color);//Machine->scrbitmap->line[i][x] = color;
		
    	}
    
    	osd_mark_dirty(x,y,x,y+h-1,1);
    }
    
    public static void drawhline(int x, int w, int y, char color)
    {
    	if ((Machine.ui_orientation & ORIENTATION_SWAP_XY)!=0)
    	{
    		if ((Machine.ui_orientation & ORIENTATION_FLIP_X)!=0)
    			y = Machine.scrbitmap.width - y - 1;
    		if ((Machine.ui_orientation & ORIENTATION_FLIP_Y)!=0)
    			x = Machine.scrbitmap.height - x - w;
    
    		drawvline_norotate(y,x,w,color);
    	}
    	else
    	{
    		if ((Machine.ui_orientation & ORIENTATION_FLIP_X)!=0)
    			x = Machine.scrbitmap.width - x - w;
    		if ((Machine.ui_orientation & ORIENTATION_FLIP_Y)!=0)
    			y = Machine.scrbitmap.height - y - 1;
    
    		drawhline_norotate(x,w,y,color);
    	}
    }
    
    public static void drawvline(int x, int y, int h, char color)
    {
    	if ((Machine.ui_orientation & ORIENTATION_SWAP_XY)!=0)
    	{
    		if ((Machine.ui_orientation & ORIENTATION_FLIP_X)!=0)
    			y = Machine.scrbitmap.width - y - h;
    		if ((Machine.ui_orientation & ORIENTATION_FLIP_Y)!=0)
    			x = Machine.scrbitmap.height - x - 1;
    
    		drawhline_norotate(y,h,x,color);
    	}
    	else
    	{
    		if ((Machine.ui_orientation & ORIENTATION_FLIP_X)!=0)
    			x = Machine.scrbitmap.width - x - 1;
    		if ((Machine.ui_orientation & ORIENTATION_FLIP_Y)!=0)
    			y = Machine.scrbitmap.height - y - h;
    
    		drawvline_norotate(x,y,h,color);
    	}
    }
    
    
    public static void ui_drawbox(int leftx,int topy,int width,int height)
    {
    	int y;
    	char black,white;
    
    
    	if (leftx < 0) leftx = 0;
    	if (topy < 0) topy = 0;
    	if (width > Machine.uiwidth) width = Machine.uiwidth;
    	if (height > Machine.uiheight) height = Machine.uiheight;
    
    	leftx += Machine.uixmin;
    	topy += Machine.uiymin;
    
    	black = Machine.uifont.colortable.read(0);
    	white = Machine.uifont.colortable.read(1);
    
    	drawhline(leftx,width,topy, 		white);
    	drawhline(leftx,width,topy+height-1,white);
    	drawvline(leftx,		topy,height,white);
    	drawvline(leftx+width-1,topy,height,white);
    	for (y = topy+1;y < topy+height-1;y++)
    		drawhline(leftx+1,width-2,y,black);
    }
    /*TODO*///
    /*TODO*///
    /*TODO*///static void drawbar(int leftx,int topy,int width,int height,int percentage,int default_percentage)
    /*TODO*///{
    /*TODO*///	int y;
    /*TODO*///	unsigned short black,white;
    /*TODO*///
    /*TODO*///
    /*TODO*///	if (leftx < 0) leftx = 0;
    /*TODO*///	if (topy < 0) topy = 0;
    /*TODO*///	if (width > Machine->uiwidth) width = Machine->uiwidth;
    /*TODO*///	if (height > Machine->uiheight) height = Machine->uiheight;
    /*TODO*///
    /*TODO*///	leftx += Machine->uixmin;
    /*TODO*///	topy += Machine->uiymin;
    /*TODO*///
    /*TODO*///	black = Machine->uifont->colortable[0];
    /*TODO*///	white = Machine->uifont->colortable[1];
    /*TODO*///
    /*TODO*///	for (y = topy;y < topy + height/8;y++)
    /*TODO*///		drawpixel(leftx+(width-1)*default_percentage/100, y, white);
    /*TODO*///
    /*TODO*///	drawhline(leftx,width,topy+height/8,white);
    /*TODO*///
    /*TODO*///	for (y = topy+height/8;y < topy+height-height/8;y++)
    /*TODO*///		drawhline(leftx,1+(width-1)*percentage/100,y,white);
    /*TODO*///
    /*TODO*///	drawhline(leftx,width,topy+height-height/8-1,white);
    /*TODO*///
    /*TODO*///	for (y = topy+height-height/8;y < topy + height;y++)
    /*TODO*///		drawpixel(leftx+(width-1)*default_percentage/100, y, white);
    /*TODO*///}
    /*TODO*///
    /*TODO*////* Extract one line from a multiline buffer */
    /*TODO*////* Return the characters number of the line, pbegin point to the start of the next line */
    /*TODO*///static unsigned multiline_extract(const char** pbegin, const char* end, unsigned max)
    /*TODO*///{
    /*TODO*///	unsigned mac = 0;
    /*TODO*///	const char* begin = *pbegin;
    /*TODO*///	while (begin != end && mac < max)
    /*TODO*///	{
    /*TODO*///		if (*begin == '\n')
    /*TODO*///		{
    /*TODO*///			*pbegin = begin + 1; /* strip final space */
    /*TODO*///			return mac;
    /*TODO*///		}
    /*TODO*///		else if (*begin == ' ')
    /*TODO*///		{
    /*TODO*///			const char* word_end = begin + 1;
    /*TODO*///			while (word_end != end && *word_end != ' ' && *word_end != '\n')
    /*TODO*///				++word_end;
    /*TODO*///			if (mac + word_end - begin > max)
    /*TODO*///			{
    /*TODO*///				if (mac)
    /*TODO*///				{
    /*TODO*///					*pbegin = begin + 1;
    /*TODO*///					return mac; /* strip final space */
    /*TODO*///				} else {
    /*TODO*///					*pbegin = begin + max;
    /*TODO*///					return max;
    /*TODO*///				}
    /*TODO*///			}
    /*TODO*///			mac += word_end - begin;
    /*TODO*///			begin = word_end;
    /*TODO*///		} else {
    /*TODO*///			++mac;
    /*TODO*///			++begin;
    /*TODO*///		}
    /*TODO*///	}
    /*TODO*///	if (begin != end && (*begin == '\n' || *begin == ' '))
    /*TODO*///		++begin;
    /*TODO*///	*pbegin = begin;
    /*TODO*///	return mac;
    /*TODO*///}
    /*TODO*///
    /*TODO*////* Compute the output size of a multiline string */
    /*TODO*///static void multiline_size(int* dx, int* dy, const char* begin, const char* end, unsigned max)
    /*TODO*///{
    /*TODO*///	unsigned rows = 0;
    /*TODO*///	unsigned cols = 0;
    /*TODO*///	while (begin != end)
    /*TODO*///	{
    /*TODO*///		unsigned len;
    /*TODO*///		len = multiline_extract(&begin,end,max);
    /*TODO*///		if (len > cols)
    /*TODO*///			cols = len;
    /*TODO*///		++rows;
    /*TODO*///	}
    /*TODO*///	*dx = cols * Machine->uifontwidth;
    /*TODO*///	*dy = (rows-1) * 3*Machine->uifontheight/2 + Machine->uifontheight;
    /*TODO*///}
    /*TODO*///
    /*TODO*////* Compute the output size of a multiline string with box */
    /*TODO*///static void multilinebox_size(int* dx, int* dy, const char* begin, const char* end, unsigned max)
    /*TODO*///{
    /*TODO*///	multiline_size(dx,dy,begin,end,max);
    /*TODO*///	*dx += Machine->uifontwidth;
    /*TODO*///	*dy += Machine->uifontheight;
    /*TODO*///}
    /*TODO*///
    /*TODO*////* Display a multiline string */
    /*TODO*///static void ui_multitext_ex(const char* begin, const char* end, unsigned max, int x, int y, int color)
    /*TODO*///{
    /*TODO*///	while (begin != end)
    /*TODO*///	{
    /*TODO*///		const char* line_begin = begin;
    /*TODO*///		unsigned len = multiline_extract(&begin,end,max);
    /*TODO*///		ui_text_ex(line_begin, line_begin + len,x,y,color);
    /*TODO*///		y += 3*Machine->uifontheight/2;
    /*TODO*///	}
    /*TODO*///}
    /*TODO*///
    /*TODO*////* Display a multiline string with box */
    /*TODO*///static void ui_multitextbox_ex(const char* begin, const char* end, unsigned max, int x, int y, int dx, int dy, int color)
    /*TODO*///{
    /*TODO*///	ui_drawbox(x,y,dx,dy);
    /*TODO*///	x += Machine->uifontwidth/2;
    /*TODO*///	y += Machine->uifontheight/2;
    /*TODO*///	ui_multitext_ex(begin,end,max,x,y,color);
    /*TODO*///}
    /*TODO*///
    /*TODO*///void ui_displaymenu(const char **items,const char **subitems,char *flag,int selected,int arrowize_subitem)
    /*TODO*///{
    /*TODO*///	struct DisplayText dt[256];
    /*TODO*///	int curr_dt;
    /*TODO*///	char lefthilight[2] = "\x1a";
    /*TODO*///	char righthilight[2] = "\x1b";
    /*TODO*///	char uparrow[2] = "\x18";
    /*TODO*///	char downarrow[2] = "\x19";
    /*TODO*///	char leftarrow[2] = "\x11";
    /*TODO*///	char rightarrow[2] = "\x10";
    /*TODO*///	int i,count,len,maxlen,highlen;
    /*TODO*///	int leftoffs,topoffs,visible,topitem;
    /*TODO*///	int selected_long;
    /*TODO*///
    /*TODO*///
    /*TODO*///	i = 0;
    /*TODO*///	maxlen = 0;
    /*TODO*///	highlen = Machine->uiwidth / Machine->uifontwidth;
    /*TODO*///	while (items[i])
    /*TODO*///	{
    /*TODO*///		len = 3 + strlen(items[i]);
    /*TODO*///		if (subitems && subitems[i])
    /*TODO*///			len += 2 + strlen(subitems[i]);
    /*TODO*///		if (len > maxlen && len <= highlen)
    /*TODO*///			maxlen = len;
    /*TODO*///		i++;
    /*TODO*///	}
    /*TODO*///	count = i;
    /*TODO*///
    /*TODO*///	visible = Machine->uiheight / (3 * Machine->uifontheight / 2) - 1;
    /*TODO*///	topitem = 0;
    /*TODO*///	if (visible > count) visible = count;
    /*TODO*///	else
    /*TODO*///	{
    /*TODO*///		topitem = selected - visible / 2;
    /*TODO*///		if (topitem < 0) topitem = 0;
    /*TODO*///		if (topitem > count - visible) topitem = count - visible;
    /*TODO*///	}
    /*TODO*///
    /*TODO*///	leftoffs = (Machine->uiwidth - maxlen * Machine->uifontwidth) / 2;
    /*TODO*///	topoffs = (Machine->uiheight - (3 * visible + 1) * Machine->uifontheight / 2) / 2;
    /*TODO*///
    /*TODO*///	/* black background */
    /*TODO*///	ui_drawbox(leftoffs,topoffs,maxlen * Machine->uifontwidth,(3 * visible + 1) * Machine->uifontheight / 2);
    /*TODO*///
    /*TODO*///	selected_long = 0;
    /*TODO*///	curr_dt = 0;
    /*TODO*///	for (i = 0;i < visible;i++)
    /*TODO*///	{
    /*TODO*///		int item = i + topitem;
    /*TODO*///
    /*TODO*///		if (i == 0 && item > 0)
    /*TODO*///		{
    /*TODO*///			dt[curr_dt].text = uparrow;
    /*TODO*///			dt[curr_dt].color = DT_COLOR_WHITE;
    /*TODO*///			dt[curr_dt].x = (Machine->uiwidth - Machine->uifontwidth * strlen(uparrow)) / 2;
    /*TODO*///			dt[curr_dt].y = topoffs + (3*i+1)*Machine->uifontheight/2;
    /*TODO*///			curr_dt++;
    /*TODO*///		}
    /*TODO*///		else if (i == visible - 1 && item < count - 1)
    /*TODO*///		{
    /*TODO*///			dt[curr_dt].text = downarrow;
    /*TODO*///			dt[curr_dt].color = DT_COLOR_WHITE;
    /*TODO*///			dt[curr_dt].x = (Machine->uiwidth - Machine->uifontwidth * strlen(downarrow)) / 2;
    /*TODO*///			dt[curr_dt].y = topoffs + (3*i+1)*Machine->uifontheight/2;
    /*TODO*///			curr_dt++;
    /*TODO*///		}
    /*TODO*///		else
    /*TODO*///		{
    /*TODO*///			if (subitems && subitems[item])
    /*TODO*///			{
    /*TODO*///				int sublen;
    /*TODO*///				len = strlen(items[item]);
    /*TODO*///				dt[curr_dt].text = items[item];
    /*TODO*///				dt[curr_dt].color = DT_COLOR_WHITE;
    /*TODO*///				dt[curr_dt].x = leftoffs + 3*Machine->uifontwidth/2;
    /*TODO*///				dt[curr_dt].y = topoffs + (3*i+1)*Machine->uifontheight/2;
    /*TODO*///				curr_dt++;
    /*TODO*///				sublen = strlen(subitems[item]);
    /*TODO*///				if (sublen > maxlen-5-len)
    /*TODO*///				{
    /*TODO*///					dt[curr_dt].text = "...";
    /*TODO*///					sublen = strlen(dt[curr_dt].text);
    /*TODO*///					if (item == selected)
    /*TODO*///						selected_long = 1;
    /*TODO*///				} else {
    /*TODO*///					dt[curr_dt].text = subitems[item];
    /*TODO*///				}
    /*TODO*///				/* If this item is flagged, draw it in inverse print */
    /*TODO*///				dt[curr_dt].color = (flag && flag[item]) ? DT_COLOR_YELLOW : DT_COLOR_WHITE;
    /*TODO*///				dt[curr_dt].x = leftoffs + Machine->uifontwidth * (maxlen-1-sublen) - Machine->uifontwidth/2;
    /*TODO*///				dt[curr_dt].y = topoffs + (3*i+1)*Machine->uifontheight/2;
    /*TODO*///				curr_dt++;
    /*TODO*///			}
    /*TODO*///			else
    /*TODO*///			{
    /*TODO*///				dt[curr_dt].text = items[item];
    /*TODO*///				dt[curr_dt].color = DT_COLOR_WHITE;
    /*TODO*///				dt[curr_dt].x = (Machine->uiwidth - Machine->uifontwidth * strlen(items[item])) / 2;
    /*TODO*///				dt[curr_dt].y = topoffs + (3*i+1)*Machine->uifontheight/2;
    /*TODO*///				curr_dt++;
    /*TODO*///			}
    /*TODO*///		}
    /*TODO*///	}
    /*TODO*///
    /*TODO*///	i = selected - topitem;
    /*TODO*///	if (subitems && subitems[selected] && arrowize_subitem)
    /*TODO*///	{
    /*TODO*///		if (arrowize_subitem & 1)
    /*TODO*///		{
    /*TODO*///			dt[curr_dt].text = leftarrow;
    /*TODO*///			dt[curr_dt].color = DT_COLOR_WHITE;
    /*TODO*///			dt[curr_dt].x = leftoffs + Machine->uifontwidth * (maxlen-2 - strlen(subitems[selected])) - Machine->uifontwidth/2 - 1;
    /*TODO*///			dt[curr_dt].y = topoffs + (3*i+1)*Machine->uifontheight/2;
    /*TODO*///			curr_dt++;
    /*TODO*///		}
    /*TODO*///		if (arrowize_subitem & 2)
    /*TODO*///		{
    /*TODO*///			dt[curr_dt].text = rightarrow;
    /*TODO*///			dt[curr_dt].color = DT_COLOR_WHITE;
    /*TODO*///			dt[curr_dt].x = leftoffs + Machine->uifontwidth * (maxlen-1) - Machine->uifontwidth/2;
    /*TODO*///			dt[curr_dt].y = topoffs + (3*i+1)*Machine->uifontheight/2;
    /*TODO*///			curr_dt++;
    /*TODO*///		}
    /*TODO*///	}
    /*TODO*///	else
    /*TODO*///	{
    /*TODO*///		dt[curr_dt].text = righthilight;
    /*TODO*///		dt[curr_dt].color = DT_COLOR_WHITE;
    /*TODO*///		dt[curr_dt].x = leftoffs + Machine->uifontwidth * (maxlen-1) - Machine->uifontwidth/2;
    /*TODO*///		dt[curr_dt].y = topoffs + (3*i+1)*Machine->uifontheight/2;
    /*TODO*///		curr_dt++;
    /*TODO*///	}
    /*TODO*///	dt[curr_dt].text = lefthilight;
    /*TODO*///	dt[curr_dt].color = DT_COLOR_WHITE;
    /*TODO*///	dt[curr_dt].x = leftoffs + Machine->uifontwidth/2;
    /*TODO*///	dt[curr_dt].y = topoffs + (3*i+1)*Machine->uifontheight/2;
    /*TODO*///	curr_dt++;
    /*TODO*///
    /*TODO*///	dt[curr_dt].text = 0;	/* terminate array */
    /*TODO*///
    /*TODO*///	displaytext(dt,0,0);
    /*TODO*///
    /*TODO*///	if (selected_long)
    /*TODO*///	{
    /*TODO*///		int long_dx;
    /*TODO*///		int long_dy;
    /*TODO*///		int long_x;
    /*TODO*///		int long_y;
    /*TODO*///		unsigned long_max;
    /*TODO*///
    /*TODO*///		long_max = (Machine->uiwidth / Machine->uifontwidth) - 2;
    /*TODO*///		multilinebox_size(&long_dx,&long_dy,subitems[selected],subitems[selected] + strlen(subitems[selected]), long_max);
    /*TODO*///
    /*TODO*///		long_x = Machine->uiwidth - long_dx;
    /*TODO*///		long_y = topoffs + (i+1) * 3*Machine->uifontheight/2;
    /*TODO*///
    /*TODO*///		/* if too low display up */
    /*TODO*///		if (long_y + long_dy > Machine->uiheight)
    /*TODO*///			long_y = topoffs + i * 3*Machine->uifontheight/2 - long_dy;
    /*TODO*///
    /*TODO*///		ui_multitextbox_ex(subitems[selected],subitems[selected] + strlen(subitems[selected]), long_max, long_x,long_y,long_dx,long_dy, DT_COLOR_WHITE);
    /*TODO*///	}
    /*TODO*///}
    /*TODO*///
    /*TODO*///
    public static void ui_displaymessagewindow(String text)
    {

        DisplayText[] dt = DisplayText.create(256);
        int curr_dt;
        int c, c2;//use them as counters (shadow)
        char[] textcopy = new char[2048];
        int i, len, maxlen, lines;
        int leftoffs, topoffs;
        int maxcols, maxrows;

    
    	maxcols = (Machine.uiwidth / Machine.uifontwidth) - 1;
    	maxrows = (2 * Machine.uiheight - Machine.uifontheight) / (3 * Machine.uifontheight);
    
    	/* copy text, calculate max len, count lines, wrap long lines and crop height to fit */
    	 maxlen = 0;
         lines = 0;
         c = 0;//(char *)text;
         c2 = 0;//textcopy;
         while (c < text.length() && text.charAt(c) != '\0')
         {
    		len = 0;
                while (c < text.length() && text.charAt(c) != '\0' && text.charAt(c) != '\n')
                {
                    textcopy[c2++] = text.charAt(c++);
                    len++;
                    if (len == maxcols && text.charAt(c) != '\n')
                    {
                        /* attempt word wrap */
                        int csave = c, c2save = c2;
                        int lensave = len;

                        /* back up to last space or beginning of line */
                        while (text.charAt(c) != ' ' && text.charAt(c) != '\n' && c > 0)
                        {
                            --c; --c2; --len;
                        }
                        /* if no space was found, hard wrap instead */
                        if (text.charAt(c) != ' ')
                        {
                            c = csave; c2 = c2save; len = lensave;
                        }
                        else
                            c++;

                        textcopy[c2++] = '\n'; /* insert wrap */
                        break;
                    }		
    		}
                if (c < text.length() && text.charAt(c) == '\n')
                    textcopy[c2++] = text.charAt(c++);
  
    		if (len > maxlen) maxlen = len;
    
    		lines++;
    		if (lines == maxrows)
    			break;
    	}
    	textcopy[c2] = '\0';
    
    	maxlen += 1;
    
    	leftoffs = (Machine.uiwidth - Machine.uifontwidth * maxlen) / 2;
    	if (leftoffs < 0) leftoffs = 0;
    	topoffs = (Machine.uiheight - (3 * lines + 1) * Machine.uifontheight / 2) / 2;
    
    	/* black background */
    	ui_drawbox(leftoffs,topoffs,maxlen * Machine.uifontwidth,(3 * lines + 1) * Machine.uifontheight / 2);
    
    	curr_dt = 0;
    	c = 0;//textcopy;
    	i = 0;
         while (c < textcopy.length && textcopy[c] != '\0')
            {
                c2 = c;
                while (c < textcopy.length && textcopy[c] != '\0' && textcopy[c] != '\n')
                    c++;

                if (textcopy[c] == '\n')
                {
                    textcopy[c] = '\0';
                    c++;
                }

                if (textcopy[c2] == '\t')    /* center text */
                {
                    c2++;
                    dt[curr_dt].x = (Machine.uiwidth - Machine.uifontwidth * (c - c2)) / 2;
                }
                else
                    dt[curr_dt].x = leftoffs + Machine.uifontwidth / 2;

                dt[curr_dt].text = new String(textcopy).substring(c2);
                dt[curr_dt].color = DT_COLOR_WHITE;
                dt[curr_dt].y = topoffs + (3 * i + 1) * Machine.uifontheight / 2;
                curr_dt++;

                i++;
            }
    		
    	dt[curr_dt].text = null;	/* terminate array */
    
    	displaytext(dt,0,0);
    }
    /*TODO*///
    /*TODO*///
    /*TODO*///
    /*TODO*///#ifndef NEOFREE
    /*TODO*///#ifndef TINY_COMPILE
    /*TODO*///extern int no_of_tiles;
    /*TODO*///void NeoMVSDrawGfx(unsigned char **line,const struct GfxElement *gfx,
    /*TODO*///		unsigned int code,unsigned int color,int flipx,int flipy,int sx,int sy,
    /*TODO*///		int zx,int zy,const struct rectangle *clip);
    /*TODO*///void NeoMVSDrawGfx16(unsigned char **line,const struct GfxElement *gfx,
    /*TODO*///		unsigned int code,unsigned int color,int flipx,int flipy,int sx,int sy,
    /*TODO*///		int zx,int zy,const struct rectangle *clip);
    /*TODO*///extern struct GameDriver driver_neogeo;
    /*TODO*///#endif
    /*TODO*///#endif
    /*TODO*///
    /*TODO*///static void showcharset(void)
    /*TODO*///{
    /*TODO*///	int i;
    /*TODO*///	char buf[80];
    /*TODO*///	int bank,color,firstdrawn;
    /*TODO*///	int palpage;
    /*TODO*///	int trueorientation;
    /*TODO*///	int changed;
    /*TODO*///	int game_is_neogeo=0;
    /*TODO*///	unsigned char *orig_used_colors=0;
    /*TODO*///
    /*TODO*///
    /*TODO*///	if (palette_used_colors)
    /*TODO*///	{
    /*TODO*///		orig_used_colors = malloc(Machine->drv->total_colors * sizeof(unsigned char));
    /*TODO*///		if (!orig_used_colors) return;
    /*TODO*///
    /*TODO*///		memcpy(orig_used_colors,palette_used_colors,Machine->drv->total_colors * sizeof(unsigned char));
    /*TODO*///	}
    /*TODO*///
    /*TODO*///#ifndef NEOFREE
    /*TODO*///#ifndef TINY_COMPILE
    /*TODO*///	if (Machine->gamedrv->clone_of == &driver_neogeo ||
    /*TODO*///			(Machine->gamedrv->clone_of &&
    /*TODO*///				Machine->gamedrv->clone_of->clone_of == &driver_neogeo))
    /*TODO*///		game_is_neogeo=1;
    /*TODO*///#endif
    /*TODO*///#endif
    /*TODO*///
    /*TODO*///	bank = -1;
    /*TODO*///	color = 0;
    /*TODO*///	firstdrawn = 0;
    /*TODO*///	palpage = 0;
    /*TODO*///
    /*TODO*///	changed = 1;
    /*TODO*///
    /*TODO*///	do
    /*TODO*///	{
    /*TODO*///		int cpx,cpy,skip_chars;
    /*TODO*///
    /*TODO*///		if (bank >= 0)
    /*TODO*///		{
    /*TODO*///			cpx = Machine->uiwidth / Machine->gfx[bank]->width;
    /*TODO*///			cpy = (Machine->uiheight - Machine->uifontheight) / Machine->gfx[bank]->height;
    /*TODO*///			skip_chars = cpx * cpy;
    /*TODO*///		}
    /*TODO*///		else cpx = cpy = skip_chars = 0;
    /*TODO*///
    /*TODO*///		if (changed)
    /*TODO*///		{
    /*TODO*///			int lastdrawn=0;
    /*TODO*///
    /*TODO*///			osd_clearbitmap(Machine->scrbitmap);
    /*TODO*///
    /*TODO*///			/* validity chack after char bank change */
    /*TODO*///			if (bank >= 0)
    /*TODO*///			{
    /*TODO*///				if (firstdrawn >= Machine->gfx[bank]->total_elements)
    /*TODO*///				{
    /*TODO*///					firstdrawn = Machine->gfx[bank]->total_elements - skip_chars;
    /*TODO*///					if (firstdrawn < 0) firstdrawn = 0;
    /*TODO*///				}
    /*TODO*///			}
    /*TODO*///
    /*TODO*///			if(bank!=2 || !game_is_neogeo)
    /*TODO*///			{
    /*TODO*///				if (bank >= 0)
    /*TODO*///				{
    /*TODO*///					int table_offs;
    /*TODO*///					int flipx,flipy;
    /*TODO*///
    /*TODO*///					/* hack: force the display into standard orientation to avoid */
    /*TODO*///					/* rotating the user interface */
    /*TODO*///					trueorientation = Machine->orientation;
    /*TODO*///					Machine->orientation = Machine->ui_orientation;
    /*TODO*///
    /*TODO*///					if (palette_used_colors)
    /*TODO*///					{
    /*TODO*///						memset(palette_used_colors,PALETTE_COLOR_TRANSPARENT,Machine->drv->total_colors * sizeof(unsigned char));
    /*TODO*///						table_offs = Machine->gfx[bank]->colortable - Machine->remapped_colortable
    /*TODO*///								+ Machine->gfx[bank]->color_granularity * color;
    /*TODO*///						for (i = 0;i < Machine->gfx[bank]->color_granularity;i++)
    /*TODO*///							palette_used_colors[Machine->game_colortable[table_offs + i]] = PALETTE_COLOR_USED;
    /*TODO*///						palette_recalc();	/* do it twice in case of previous overflow */
    /*TODO*///						palette_recalc();	/*(we redraw the screen only when it changes) */
    /*TODO*///					}
    /*TODO*///
    /*TODO*///#ifndef PREROTATE_GFX
    /*TODO*///					flipx = (Machine->orientation ^ trueorientation) & ORIENTATION_FLIP_X;
    /*TODO*///					flipy = (Machine->orientation ^ trueorientation) & ORIENTATION_FLIP_Y;
    /*TODO*///
    /*TODO*///					if (Machine->orientation & ORIENTATION_SWAP_XY)
    /*TODO*///					{
    /*TODO*///						int t;
    /*TODO*///						t = flipx; flipx = flipy; flipy = t;
    /*TODO*///					}
    /*TODO*///#else
    /*TODO*///					flipx = 0;
    /*TODO*///					flipy = 0;
    /*TODO*///#endif
    /*TODO*///
    /*TODO*///					for (i = 0; i+firstdrawn < Machine->gfx[bank]->total_elements && i<cpx*cpy; i++)
    /*TODO*///					{
    /*TODO*///						drawgfx(Machine->scrbitmap,Machine->gfx[bank],
    /*TODO*///								i+firstdrawn,color,  /*sprite num, color*/
    /*TODO*///								flipx,flipy,
    /*TODO*///								(i % cpx) * Machine->gfx[bank]->width + Machine->uixmin,
    /*TODO*///								Machine->uifontheight + (i / cpx) * Machine->gfx[bank]->height + Machine->uiymin,
    /*TODO*///								0,TRANSPARENCY_NONE,0);
    /*TODO*///
    /*TODO*///						lastdrawn = i+firstdrawn;
    /*TODO*///					}
    /*TODO*///
    /*TODO*///					Machine->orientation = trueorientation;
    /*TODO*///				}
    /*TODO*///				else
    /*TODO*///				{
    /*TODO*///					int sx,sy,x,y,colors;
    /*TODO*///
    /*TODO*///					colors = Machine->drv->total_colors - 256 * palpage;
    /*TODO*///					if (colors > 256) colors = 256;
    /*TODO*///					if (palette_used_colors)
    /*TODO*///					{
    /*TODO*///						memset(palette_used_colors,PALETTE_COLOR_UNUSED,Machine->drv->total_colors * sizeof(unsigned char));
    /*TODO*///						memset(palette_used_colors+256*palpage,PALETTE_COLOR_USED,colors * sizeof(unsigned char));
    /*TODO*///						palette_recalc();	/* do it twice in case of previous overflow */
    /*TODO*///						palette_recalc();	/*(we redraw the screen only when it changes) */
    /*TODO*///					}
    /*TODO*///
    /*TODO*///					for (i = 0;i < 16;i++)
    /*TODO*///					{
    /*TODO*///						char bf[40];
    /*TODO*///
    /*TODO*///						sx = 3*Machine->uifontwidth + (Machine->uifontwidth*4/3)*(i % 16);
    /*TODO*///						sprintf(bf,"%X",i);
    /*TODO*///						ui_text(bf,sx,2*Machine->uifontheight);
    /*TODO*///						if (16*i < colors)
    /*TODO*///						{
    /*TODO*///							sy = 3*Machine->uifontheight + (Machine->uifontheight)*(i % 16);
    /*TODO*///							sprintf(bf,"%3X",i+16*palpage);
    /*TODO*///							ui_text(bf,0,sy);
    /*TODO*///						}
    /*TODO*///					}
    /*TODO*///
    /*TODO*///					for (i = 0;i < colors;i++)
    /*TODO*///					{
    /*TODO*///						sx = Machine->uixmin + 3*Machine->uifontwidth + (Machine->uifontwidth*4/3)*(i % 16);
    /*TODO*///						sy = Machine->uiymin + 2*Machine->uifontheight + (Machine->uifontheight)*(i / 16) + Machine->uifontheight;
    /*TODO*///						for (y = 0;y < Machine->uifontheight;y++)
    /*TODO*///						{
    /*TODO*///							for (x = 0;x < Machine->uifontwidth*4/3;x++)
    /*TODO*///							{
    /*TODO*///								int tx,ty;
    /*TODO*///								if (Machine->ui_orientation & ORIENTATION_SWAP_XY)
    /*TODO*///								{
    /*TODO*///									ty = sx + x;
    /*TODO*///									tx = sy + y;
    /*TODO*///								}
    /*TODO*///								else
    /*TODO*///								{
    /*TODO*///									tx = sx + x;
    /*TODO*///									ty = sy + y;
    /*TODO*///								}
    /*TODO*///								if (Machine->ui_orientation & ORIENTATION_FLIP_X)
    /*TODO*///									tx = Machine->scrbitmap->width-1 - tx;
    /*TODO*///								if (Machine->ui_orientation & ORIENTATION_FLIP_Y)
    /*TODO*///									ty = Machine->scrbitmap->height-1 - ty;
    /*TODO*///
    /*TODO*///								if (Machine->scrbitmap->depth == 16)
    /*TODO*///									((unsigned short *)Machine->scrbitmap->line[ty])[tx]
    /*TODO*///											= Machine->pens[i + 256*palpage];
    /*TODO*///								else
    /*TODO*///									Machine->scrbitmap->line[ty][tx]
    /*TODO*///											= Machine->pens[i + 256*palpage];
    /*TODO*///							}
    /*TODO*///						}
    /*TODO*///					}
    /*TODO*///				}
    /*TODO*///			}
    /*TODO*///#ifndef NEOFREE
    /*TODO*///#ifndef TINY_COMPILE
    /*TODO*///			else	/* neogeo sprite tiles */
    /*TODO*///			{
    /*TODO*///				struct rectangle clip;
    /*TODO*///
    /*TODO*///				clip.min_x = Machine->uixmin;
    /*TODO*///				clip.max_x = Machine->uixmin + Machine->uiwidth - 1;
    /*TODO*///				clip.min_y = Machine->uiymin;
    /*TODO*///				clip.max_y = Machine->uiymin + Machine->uiheight - 1;
    /*TODO*///
    /*TODO*///				if (palette_used_colors)
    /*TODO*///				{
    /*TODO*///					memset(palette_used_colors,PALETTE_COLOR_TRANSPARENT,Machine->drv->total_colors * sizeof(unsigned char));
    /*TODO*///					memset(palette_used_colors+Machine->gfx[bank]->color_granularity*color,PALETTE_COLOR_USED,Machine->gfx[bank]->color_granularity * sizeof(unsigned char));
    /*TODO*///					palette_recalc();	/* do it twice in case of previous overflow */
    /*TODO*///					palette_recalc();	/*(we redraw the screen only when it changes) */
    /*TODO*///				}
    /*TODO*///
    /*TODO*///				for (i = 0; i+firstdrawn < no_of_tiles && i<cpx*cpy; i++)
    /*TODO*///				{
    /*TODO*///					if (Machine->scrbitmap->depth == 16)
    /*TODO*///						NeoMVSDrawGfx16(Machine->scrbitmap->line,Machine->gfx[bank],
    /*TODO*///							i+firstdrawn,color,  /*sprite num, color*/
    /*TODO*///							0,0,
    /*TODO*///							(i % cpx) * Machine->gfx[bank]->width + Machine->uixmin,
    /*TODO*///							Machine->uifontheight+1 + (i / cpx) * Machine->gfx[bank]->height + Machine->uiymin,
    /*TODO*///							16,16,&clip);
    /*TODO*///					else
    /*TODO*///						NeoMVSDrawGfx(Machine->scrbitmap->line,Machine->gfx[bank],
    /*TODO*///							i+firstdrawn,color,  /*sprite num, color*/
    /*TODO*///							0,0,
    /*TODO*///							(i % cpx) * Machine->gfx[bank]->width + Machine->uixmin,
    /*TODO*///							Machine->uifontheight+1 + (i / cpx) * Machine->gfx[bank]->height + Machine->uiymin,
    /*TODO*///							16,16,&clip);
    /*TODO*///
    /*TODO*///					lastdrawn = i+firstdrawn;
    /*TODO*///				}
    /*TODO*///			}
    /*TODO*///#endif
    /*TODO*///#endif
    /*TODO*///
    /*TODO*///			if (bank >= 0)
    /*TODO*///				sprintf(buf,"GFXSET %d COLOR %2X CODE %X-%X",bank,color,firstdrawn,lastdrawn);
    /*TODO*///			else
    /*TODO*///				strcpy(buf,"PALETTE");
    /*TODO*///			ui_text(buf,0,0);
    /*TODO*///
    /*TODO*///			changed = 0;
    /*TODO*///		}
    /*TODO*///
    /*TODO*///		/* Necessary to keep the video from getting stuck if a frame happens to be skipped in here */
    /*TODO*////* I beg to differ - the OS dependant code must not assume that */
    /*TODO*////* osd_skip_this_frame() is called before osd_update_video_and_audio() - NS */
    /*TODO*/////		osd_skip_this_frame();
    /*TODO*///		osd_update_video_and_audio();
    /*TODO*///
    /*TODO*///		if (code_pressed(KEYCODE_LCONTROL) || code_pressed(KEYCODE_RCONTROL))
    /*TODO*///		{
    /*TODO*///			skip_chars = cpx;
    /*TODO*///		}
    /*TODO*///		if (code_pressed(KEYCODE_LSHIFT) || code_pressed(KEYCODE_RSHIFT))
    /*TODO*///		{
    /*TODO*///			skip_chars = 1;
    /*TODO*///		}
    /*TODO*///
    /*TODO*///
    /*TODO*///		if (input_ui_pressed_repeat(IPT_UI_RIGHT,8))
    /*TODO*///		{
    /*TODO*///			if (bank+1 < MAX_GFX_ELEMENTS && Machine->gfx[bank + 1])
    /*TODO*///			{
    /*TODO*///				bank++;
    /*TODO*/////				firstdrawn = 0;
    /*TODO*///				changed = 1;
    /*TODO*///			}
    /*TODO*///		}
    /*TODO*///
    /*TODO*///		if (input_ui_pressed_repeat(IPT_UI_LEFT,8))
    /*TODO*///		{
    /*TODO*///			if (bank > -1)
    /*TODO*///			{
    /*TODO*///				bank--;
    /*TODO*/////				firstdrawn = 0;
    /*TODO*///				changed = 1;
    /*TODO*///			}
    /*TODO*///		}
    /*TODO*///
    /*TODO*///		if (code_pressed_memory_repeat(KEYCODE_PGDN,4))
    /*TODO*///		{
    /*TODO*///			if (bank >= 0)
    /*TODO*///			{
    /*TODO*///				if (firstdrawn + skip_chars < Machine->gfx[bank]->total_elements)
    /*TODO*///				{
    /*TODO*///					firstdrawn += skip_chars;
    /*TODO*///					changed = 1;
    /*TODO*///				}
    /*TODO*///			}
    /*TODO*///			else
    /*TODO*///			{
    /*TODO*///				if (256 * (palpage + 1) < Machine->drv->total_colors)
    /*TODO*///				{
    /*TODO*///					palpage++;
    /*TODO*///					changed = 1;
    /*TODO*///				}
    /*TODO*///			}
    /*TODO*///		}
    /*TODO*///
    /*TODO*///		if (code_pressed_memory_repeat(KEYCODE_PGUP,4))
    /*TODO*///		{
    /*TODO*///			if (bank >= 0)
    /*TODO*///			{
    /*TODO*///				firstdrawn -= skip_chars;
    /*TODO*///				if (firstdrawn < 0) firstdrawn = 0;
    /*TODO*///				changed = 1;
    /*TODO*///			}
    /*TODO*///			else
    /*TODO*///			{
    /*TODO*///				if (palpage > 0)
    /*TODO*///				{
    /*TODO*///					palpage--;
    /*TODO*///					changed = 1;
    /*TODO*///				}
    /*TODO*///			}
    /*TODO*///		}
    /*TODO*///
    /*TODO*///		if (input_ui_pressed_repeat(IPT_UI_UP,6))
    /*TODO*///		{
    /*TODO*///			if (bank >= 0)
    /*TODO*///			{
    /*TODO*///				if (color < Machine->gfx[bank]->total_colors - 1)
    /*TODO*///				{
    /*TODO*///					color++;
    /*TODO*///					changed = 1;
    /*TODO*///				}
    /*TODO*///			}
    /*TODO*///		}
    /*TODO*///
    /*TODO*///		if (input_ui_pressed_repeat(IPT_UI_DOWN,6))
    /*TODO*///		{
    /*TODO*///			if (color > 0)
    /*TODO*///			{
    /*TODO*///				color--;
    /*TODO*///				changed = 1;
    /*TODO*///			}
    /*TODO*///		}
    /*TODO*///
    /*TODO*///		if (input_ui_pressed(IPT_UI_SNAPSHOT))
    /*TODO*///			osd_save_snapshot();
    /*TODO*///	} while (!input_ui_pressed(IPT_UI_SHOW_GFX) &&
    /*TODO*///			!input_ui_pressed(IPT_UI_CANCEL));
    /*TODO*///
    /*TODO*///	/* clear the screen before returning */
    /*TODO*///	osd_clearbitmap(Machine->scrbitmap);
    /*TODO*///
    /*TODO*///	if (palette_used_colors)
    /*TODO*///	{
    /*TODO*///		/* this should force a full refresh by the video driver */
    /*TODO*///		memset(palette_used_colors,PALETTE_COLOR_TRANSPARENT,Machine->drv->total_colors * sizeof(unsigned char));
    /*TODO*///		palette_recalc();
    /*TODO*///		/* restore the game used colors array */
    /*TODO*///		memcpy(palette_used_colors,orig_used_colors,Machine->drv->total_colors * sizeof(unsigned char));
    /*TODO*///		free(orig_used_colors);
    /*TODO*///	}
    /*TODO*///
    /*TODO*///	return;
    /*TODO*///}
    /*TODO*///
    /*TODO*///
    /*TODO*///#ifdef MAME_DEBUG
    /*TODO*///static void showtotalcolors(void)
    /*TODO*///{
    /*TODO*///	char *used;
    /*TODO*///	int i,l,x,y,total;
    /*TODO*///	unsigned char r,g,b;
    /*TODO*///	char buf[40];
    /*TODO*///	int trueorientation;
    /*TODO*///
    /*TODO*///
    /*TODO*///	used = malloc(64*64*64);
    /*TODO*///	if (!used) return;
    /*TODO*///
    /*TODO*///	for (i = 0;i < 64*64*64;i++)
    /*TODO*///		used[i] = 0;
    /*TODO*///
    /*TODO*///	if (Machine->scrbitmap->depth == 16)
    /*TODO*///	{
    /*TODO*///		for (y = 0;y < Machine->scrbitmap->height;y++)
    /*TODO*///		{
    /*TODO*///			for (x = 0;x < Machine->scrbitmap->width;x++)
    /*TODO*///			{
    /*TODO*///				osd_get_pen(((unsigned short *)Machine->scrbitmap->line[y])[x],&r,&g,&b);
    /*TODO*///				r >>= 2;
    /*TODO*///				g >>= 2;
    /*TODO*///				b >>= 2;
    /*TODO*///				used[64*64*r+64*g+b] = 1;
    /*TODO*///			}
    /*TODO*///		}
    /*TODO*///	}
    /*TODO*///	else
    /*TODO*///	{
    /*TODO*///		for (y = 0;y < Machine->scrbitmap->height;y++)
    /*TODO*///		{
    /*TODO*///			for (x = 0;x < Machine->scrbitmap->width;x++)
    /*TODO*///			{
    /*TODO*///				osd_get_pen(Machine->scrbitmap->line[y][x],&r,&g,&b);
    /*TODO*///				r >>= 2;
    /*TODO*///				g >>= 2;
    /*TODO*///				b >>= 2;
    /*TODO*///				used[64*64*r+64*g+b] = 1;
    /*TODO*///			}
    /*TODO*///		}
    /*TODO*///	}
    /*TODO*///
    /*TODO*///	total = 0;
    /*TODO*///	for (i = 0;i < 64*64*64;i++)
    /*TODO*///		if (used[i]) total++;
    /*TODO*///
    /*TODO*///	/* hack: force the display into standard orientation to avoid */
    /*TODO*///	/* rotating the text */
    /*TODO*///	trueorientation = Machine->orientation;
    /*TODO*///	Machine->orientation = Machine->ui_orientation;
    /*TODO*///
    /*TODO*///	sprintf(buf,"%5d colors",total);
    /*TODO*///	l = strlen(buf);
    /*TODO*///	for (i = 0;i < l;i++)
    /*TODO*///		drawgfx(Machine->scrbitmap,Machine->uifont,buf[i],total>256?DT_COLOR_YELLOW:DT_COLOR_WHITE,0,0,Machine->uixmin+i*Machine->uifontwidth,Machine->uiymin,0,TRANSPARENCY_NONE,0);
    /*TODO*///
    /*TODO*///	Machine->orientation = trueorientation;
    /*TODO*///
    /*TODO*///	free(used);
    /*TODO*///}
    /*TODO*///#endif
    /*TODO*///
    /*TODO*///
    /*TODO*///static int setdipswitches(int selected)
    /*TODO*///{
    /*TODO*///	const char *menu_item[128];
    /*TODO*///	const char *menu_subitem[128];
    /*TODO*///	struct InputPort *entry[128];
    /*TODO*///	char flag[40];
    /*TODO*///	int i,sel;
    /*TODO*///	struct InputPort *in;
    /*TODO*///	int total;
    /*TODO*///	int arrowize;
    /*TODO*///
    /*TODO*///
    /*TODO*///	sel = selected - 1;
    /*TODO*///
    /*TODO*///
    /*TODO*///	in = Machine->input_ports;
    /*TODO*///
    /*TODO*///	total = 0;
    /*TODO*///	while (in->type != IPT_END)
    /*TODO*///	{
    /*TODO*///		if ((in->type & ~IPF_MASK) == IPT_DIPSWITCH_NAME && input_port_name(in) != 0 &&
    /*TODO*///				(in->type & IPF_UNUSED) == 0 &&
    /*TODO*///				!(!options.cheat && (in->type & IPF_CHEAT)))
    /*TODO*///		{
    /*TODO*///			entry[total] = in;
    /*TODO*///			menu_item[total] = input_port_name(in);
    /*TODO*///
    /*TODO*///			total++;
    /*TODO*///		}
    /*TODO*///
    /*TODO*///		in++;
    /*TODO*///	}
    /*TODO*///
    /*TODO*///	if (total == 0) return 0;
    /*TODO*///
    /*TODO*///	menu_item[total] = "Return to Main Menu";
    /*TODO*///	menu_item[total + 1] = 0;	/* terminate array */
    /*TODO*///	total++;
    /*TODO*///
    /*TODO*///
    /*TODO*///	for (i = 0;i < total;i++)
    /*TODO*///	{
    /*TODO*///		flag[i] = 0; /* TODO: flag the dip if it's not the real default */
    /*TODO*///		if (i < total - 1)
    /*TODO*///		{
    /*TODO*///			in = entry[i] + 1;
    /*TODO*///			while ((in->type & ~IPF_MASK) == IPT_DIPSWITCH_SETTING &&
    /*TODO*///					in->default_value != entry[i]->default_value)
    /*TODO*///				in++;
    /*TODO*///
    /*TODO*///			if ((in->type & ~IPF_MASK) != IPT_DIPSWITCH_SETTING)
    /*TODO*///				menu_subitem[i] = "INVALID";
    /*TODO*///			else menu_subitem[i] = input_port_name(in);
    /*TODO*///		}
    /*TODO*///		else menu_subitem[i] = 0;	/* no subitem */
    /*TODO*///	}
    /*TODO*///
    /*TODO*///	arrowize = 0;
    /*TODO*///	if (sel < total - 1)
    /*TODO*///	{
    /*TODO*///		in = entry[sel] + 1;
    /*TODO*///		while ((in->type & ~IPF_MASK) == IPT_DIPSWITCH_SETTING &&
    /*TODO*///				in->default_value != entry[sel]->default_value)
    /*TODO*///			in++;
    /*TODO*///
    /*TODO*///		if ((in->type & ~IPF_MASK) != IPT_DIPSWITCH_SETTING)
    /*TODO*///			/* invalid setting: revert to a valid one */
    /*TODO*///			arrowize |= 1;
    /*TODO*///		else
    /*TODO*///		{
    /*TODO*///			if (((in-1)->type & ~IPF_MASK) == IPT_DIPSWITCH_SETTING &&
    /*TODO*///					!(!options.cheat && ((in-1)->type & IPF_CHEAT)))
    /*TODO*///				arrowize |= 1;
    /*TODO*///		}
    /*TODO*///	}
    /*TODO*///	if (sel < total - 1)
    /*TODO*///	{
    /*TODO*///		in = entry[sel] + 1;
    /*TODO*///		while ((in->type & ~IPF_MASK) == IPT_DIPSWITCH_SETTING &&
    /*TODO*///				in->default_value != entry[sel]->default_value)
    /*TODO*///			in++;
    /*TODO*///
    /*TODO*///		if ((in->type & ~IPF_MASK) != IPT_DIPSWITCH_SETTING)
    /*TODO*///			/* invalid setting: revert to a valid one */
    /*TODO*///			arrowize |= 2;
    /*TODO*///		else
    /*TODO*///		{
    /*TODO*///			if (((in+1)->type & ~IPF_MASK) == IPT_DIPSWITCH_SETTING &&
    /*TODO*///					!(!options.cheat && ((in+1)->type & IPF_CHEAT)))
    /*TODO*///				arrowize |= 2;
    /*TODO*///		}
    /*TODO*///	}
    /*TODO*///
    /*TODO*///	ui_displaymenu(menu_item,menu_subitem,flag,sel,arrowize);
    /*TODO*///
    /*TODO*///	if (input_ui_pressed_repeat(IPT_UI_DOWN,8))
    /*TODO*///		sel = (sel + 1) % total;
    /*TODO*///
    /*TODO*///	if (input_ui_pressed_repeat(IPT_UI_UP,8))
    /*TODO*///		sel = (sel + total - 1) % total;
    /*TODO*///
    /*TODO*///	if (input_ui_pressed_repeat(IPT_UI_RIGHT,8))
    /*TODO*///	{
    /*TODO*///		if (sel < total - 1)
    /*TODO*///		{
    /*TODO*///			in = entry[sel] + 1;
    /*TODO*///			while ((in->type & ~IPF_MASK) == IPT_DIPSWITCH_SETTING &&
    /*TODO*///					in->default_value != entry[sel]->default_value)
    /*TODO*///				in++;
    /*TODO*///
    /*TODO*///			if ((in->type & ~IPF_MASK) != IPT_DIPSWITCH_SETTING)
    /*TODO*///				/* invalid setting: revert to a valid one */
    /*TODO*///				entry[sel]->default_value = (entry[sel]+1)->default_value & entry[sel]->mask;
    /*TODO*///			else
    /*TODO*///			{
    /*TODO*///				if (((in+1)->type & ~IPF_MASK) == IPT_DIPSWITCH_SETTING &&
    /*TODO*///						!(!options.cheat && ((in+1)->type & IPF_CHEAT)))
    /*TODO*///					entry[sel]->default_value = (in+1)->default_value & entry[sel]->mask;
    /*TODO*///			}
    /*TODO*///
    /*TODO*///			/* tell updatescreen() to clean after us (in case the window changes size) */
    /*TODO*///			need_to_clear_bitmap = 1;
    /*TODO*///		}
    /*TODO*///	}
    /*TODO*///
    /*TODO*///	if (input_ui_pressed_repeat(IPT_UI_LEFT,8))
    /*TODO*///	{
    /*TODO*///		if (sel < total - 1)
    /*TODO*///		{
    /*TODO*///			in = entry[sel] + 1;
    /*TODO*///			while ((in->type & ~IPF_MASK) == IPT_DIPSWITCH_SETTING &&
    /*TODO*///					in->default_value != entry[sel]->default_value)
    /*TODO*///				in++;
    /*TODO*///
    /*TODO*///			if ((in->type & ~IPF_MASK) != IPT_DIPSWITCH_SETTING)
    /*TODO*///				/* invalid setting: revert to a valid one */
    /*TODO*///				entry[sel]->default_value = (entry[sel]+1)->default_value & entry[sel]->mask;
    /*TODO*///			else
    /*TODO*///			{
    /*TODO*///				if (((in-1)->type & ~IPF_MASK) == IPT_DIPSWITCH_SETTING &&
    /*TODO*///						!(!options.cheat && ((in-1)->type & IPF_CHEAT)))
    /*TODO*///					entry[sel]->default_value = (in-1)->default_value & entry[sel]->mask;
    /*TODO*///			}
    /*TODO*///
    /*TODO*///			/* tell updatescreen() to clean after us (in case the window changes size) */
    /*TODO*///			need_to_clear_bitmap = 1;
    /*TODO*///		}
    /*TODO*///	}
    /*TODO*///
    /*TODO*///	if (input_ui_pressed(IPT_UI_SELECT))
    /*TODO*///	{
    /*TODO*///		if (sel == total - 1) sel = -1;
    /*TODO*///	}
    /*TODO*///
    /*TODO*///	if (input_ui_pressed(IPT_UI_CANCEL))
    /*TODO*///		sel = -1;
    /*TODO*///
    /*TODO*///	if (input_ui_pressed(IPT_UI_CONFIGURE))
    /*TODO*///		sel = -2;
    /*TODO*///
    /*TODO*///	if (sel == -1 || sel == -2)
    /*TODO*///	{
    /*TODO*///		/* tell updatescreen() to clean after us */
    /*TODO*///		need_to_clear_bitmap = 1;
    /*TODO*///	}
    /*TODO*///
    /*TODO*///	return sel + 1;
    /*TODO*///}
    /*TODO*///
    /*TODO*////* This flag is used for record OR sequence of key/joy */
    /*TODO*////* when is !=0 the first sequence is record, otherwise the first free */
    /*TODO*////* it's used byt setdefkeysettings, setdefjoysettings, setkeysettings, setjoysettings */
    /*TODO*///static int record_first_insert = 1;
    /*TODO*///
    /*TODO*///static char menu_subitem_buffer[400][96];
    /*TODO*///
    /*TODO*///static int setdefcodesettings(int selected)
    /*TODO*///{
    /*TODO*///	const char *menu_item[400];
    /*TODO*///	const char *menu_subitem[400];
    /*TODO*///	struct ipd *entry[400];
    /*TODO*///	char flag[400];
    /*TODO*///	int i,sel;
    /*TODO*///	struct ipd *in;
    /*TODO*///	int total;
    /*TODO*///	extern struct ipd inputport_defaults[];
    /*TODO*///
    /*TODO*///	sel = selected - 1;
    /*TODO*///
    /*TODO*///
    /*TODO*///	if (Machine->input_ports == 0)
    /*TODO*///		return 0;
    /*TODO*///
    /*TODO*///	in = inputport_defaults;
    /*TODO*///
    /*TODO*///	total = 0;
    /*TODO*///	while (in->type != IPT_END)
    /*TODO*///	{
    /*TODO*///		if (in->name != 0  && (in->type & ~IPF_MASK) != IPT_UNKNOWN && (in->type & IPF_UNUSED) == 0
    /*TODO*///			&& !(!options.cheat && (in->type & IPF_CHEAT)))
    /*TODO*///		{
    /*TODO*///			entry[total] = in;
    /*TODO*///			menu_item[total] = in->name;
    /*TODO*///
    /*TODO*///			total++;
    /*TODO*///		}
    /*TODO*///
    /*TODO*///		in++;
    /*TODO*///	}
    /*TODO*///
    /*TODO*///	if (total == 0) return 0;
    /*TODO*///
    /*TODO*///	menu_item[total] = "Return to Main Menu";
    /*TODO*///	menu_item[total + 1] = 0;	/* terminate array */
    /*TODO*///	total++;
    /*TODO*///
    /*TODO*///	for (i = 0;i < total;i++)
    /*TODO*///	{
    /*TODO*///		if (i < total - 1)
    /*TODO*///		{
    /*TODO*///			seq_name(&entry[i]->seq,menu_subitem_buffer[i],sizeof(menu_subitem_buffer[0]));
    /*TODO*///			menu_subitem[i] = menu_subitem_buffer[i];
    /*TODO*///		} else
    /*TODO*///			menu_subitem[i] = 0;	/* no subitem */
    /*TODO*///		flag[i] = 0;
    /*TODO*///	}
    /*TODO*///
    /*TODO*///	if (sel > SEL_MASK)   /* are we waiting for a new key? */
    /*TODO*///	{
    /*TODO*///		int ret;
    /*TODO*///
    /*TODO*///		menu_subitem[sel & SEL_MASK] = "    ";
    /*TODO*///		ui_displaymenu(menu_item,menu_subitem,flag,sel & SEL_MASK,3);
    /*TODO*///
    /*TODO*///		ret = seq_read_async(&entry[sel & SEL_MASK]->seq,record_first_insert);
    /*TODO*///
    /*TODO*///		if (ret >= 0)
    /*TODO*///		{
    /*TODO*///			sel &= 0xff;
    /*TODO*///
    /*TODO*///			if (ret > 0 || seq_get_1(&entry[sel]->seq) == CODE_NONE)
    /*TODO*///			{
    /*TODO*///				seq_set_1(&entry[sel]->seq,CODE_NONE);
    /*TODO*///				ret = 1;
    /*TODO*///			}
    /*TODO*///
    /*TODO*///			/* tell updatescreen() to clean after us (in case the window changes size) */
    /*TODO*///			need_to_clear_bitmap = 1;
    /*TODO*///
    /*TODO*///			record_first_insert = ret != 0;
    /*TODO*///		}
    /*TODO*///
    /*TODO*///
    /*TODO*///		return sel + 1;
    /*TODO*///	}
    /*TODO*///
    /*TODO*///
    /*TODO*///	ui_displaymenu(menu_item,menu_subitem,flag,sel,0);
    /*TODO*///
    /*TODO*///	if (input_ui_pressed_repeat(IPT_UI_DOWN,8))
    /*TODO*///	{
    /*TODO*///		sel = (sel + 1) % total;
    /*TODO*///		record_first_insert = 1;
    /*TODO*///	}
    /*TODO*///
    /*TODO*///	if (input_ui_pressed_repeat(IPT_UI_UP,8))
    /*TODO*///	{
    /*TODO*///		sel = (sel + total - 1) % total;
    /*TODO*///		record_first_insert = 1;
    /*TODO*///	}
    /*TODO*///
    /*TODO*///	if (input_ui_pressed(IPT_UI_SELECT))
    /*TODO*///	{
    /*TODO*///		if (sel == total - 1) sel = -1;
    /*TODO*///		else
    /*TODO*///		{
    /*TODO*///			seq_read_async_start();
    /*TODO*///
    /*TODO*///			sel |= 1 << SEL_BITS;	/* we'll ask for a key */
    /*TODO*///
    /*TODO*///			/* tell updatescreen() to clean after us (in case the window changes size) */
    /*TODO*///			need_to_clear_bitmap = 1;
    /*TODO*///		}
    /*TODO*///	}
    /*TODO*///
    /*TODO*///	if (input_ui_pressed(IPT_UI_CANCEL))
    /*TODO*///		sel = -1;
    /*TODO*///
    /*TODO*///	if (input_ui_pressed(IPT_UI_CONFIGURE))
    /*TODO*///		sel = -2;
    /*TODO*///
    /*TODO*///	if (sel == -1 || sel == -2)
    /*TODO*///	{
    /*TODO*///		/* tell updatescreen() to clean after us */
    /*TODO*///		need_to_clear_bitmap = 1;
    /*TODO*///
    /*TODO*///		record_first_insert = 1;
    /*TODO*///	}
    /*TODO*///
    /*TODO*///	return sel + 1;
    /*TODO*///}
    /*TODO*///
    /*TODO*///
    /*TODO*///
    /*TODO*///static int setcodesettings(int selected)
    /*TODO*///{
    /*TODO*///	const char *menu_item[400];
    /*TODO*///	const char *menu_subitem[400];
    /*TODO*///	struct InputPort *entry[400];
    /*TODO*///	char flag[400];
    /*TODO*///	int i,sel;
    /*TODO*///	struct InputPort *in;
    /*TODO*///	int total;
    /*TODO*///
    /*TODO*///
    /*TODO*///	sel = selected - 1;
    /*TODO*///
    /*TODO*///
    /*TODO*///	if (Machine->input_ports == 0)
    /*TODO*///		return 0;
    /*TODO*///
    /*TODO*///	in = Machine->input_ports;
    /*TODO*///
    /*TODO*///	total = 0;
    /*TODO*///	while (in->type != IPT_END)
    /*TODO*///	{
    /*TODO*///		if (input_port_name(in) != 0 && seq_get_1(&in->seq) != CODE_NONE && (in->type & ~IPF_MASK) != IPT_UNKNOWN)
    /*TODO*///		{
    /*TODO*///			entry[total] = in;
    /*TODO*///			menu_item[total] = input_port_name(in);
    /*TODO*///
    /*TODO*///			total++;
    /*TODO*///		}
    /*TODO*///
    /*TODO*///		in++;
    /*TODO*///	}
    /*TODO*///
    /*TODO*///	if (total == 0) return 0;
    /*TODO*///
    /*TODO*///	menu_item[total] = "Return to Main Menu";
    /*TODO*///	menu_item[total + 1] = 0;	/* terminate array */
    /*TODO*///	total++;
    /*TODO*///
    /*TODO*///	for (i = 0;i < total;i++)
    /*TODO*///	{
    /*TODO*///		if (i < total - 1)
    /*TODO*///		{
    /*TODO*///			seq_name(input_port_seq(entry[i]),menu_subitem_buffer[i],sizeof(menu_subitem_buffer[0]));
    /*TODO*///			menu_subitem[i] = menu_subitem_buffer[i];
    /*TODO*///
    /*TODO*///			/* If the key isn't the default, flag it */
    /*TODO*///			if (seq_get_1(&entry[i]->seq) != CODE_DEFAULT)
    /*TODO*///				flag[i] = 1;
    /*TODO*///			else
    /*TODO*///				flag[i] = 0;
    /*TODO*///
    /*TODO*///		} else
    /*TODO*///			menu_subitem[i] = 0;	/* no subitem */
    /*TODO*///	}
    /*TODO*///
    /*TODO*///	if (sel > SEL_MASK)   /* are we waiting for a new key? */
    /*TODO*///	{
    /*TODO*///		int ret;
    /*TODO*///
    /*TODO*///		menu_subitem[sel & SEL_MASK] = "    ";
    /*TODO*///		ui_displaymenu(menu_item,menu_subitem,flag,sel & SEL_MASK,3);
    /*TODO*///
    /*TODO*///		ret = seq_read_async(&entry[sel & SEL_MASK]->seq,record_first_insert);
    /*TODO*///
    /*TODO*///		if (ret >= 0)
    /*TODO*///		{
    /*TODO*///			sel &= 0xff;
    /*TODO*///
    /*TODO*///			if (ret > 0 || seq_get_1(&entry[sel]->seq) == CODE_NONE)
    /*TODO*///			{
    /*TODO*///				seq_set_1(&entry[sel]->seq, CODE_DEFAULT);
    /*TODO*///				ret = 1;
    /*TODO*///			}
    /*TODO*///
    /*TODO*///			/* tell updatescreen() to clean after us (in case the window changes size) */
    /*TODO*///			need_to_clear_bitmap = 1;
    /*TODO*///
    /*TODO*///			record_first_insert = ret != 0;
    /*TODO*///		}
    /*TODO*///
    /*TODO*///		return sel + 1;
    /*TODO*///	}
    /*TODO*///
    /*TODO*///
    /*TODO*///	ui_displaymenu(menu_item,menu_subitem,flag,sel,0);
    /*TODO*///
    /*TODO*///	if (input_ui_pressed_repeat(IPT_UI_DOWN,8))
    /*TODO*///	{
    /*TODO*///		sel = (sel + 1) % total;
    /*TODO*///		record_first_insert = 1;
    /*TODO*///	}
    /*TODO*///
    /*TODO*///	if (input_ui_pressed_repeat(IPT_UI_UP,8))
    /*TODO*///	{
    /*TODO*///		sel = (sel + total - 1) % total;
    /*TODO*///		record_first_insert = 1;
    /*TODO*///	}
    /*TODO*///
    /*TODO*///	if (input_ui_pressed(IPT_UI_SELECT))
    /*TODO*///	{
    /*TODO*///		if (sel == total - 1) sel = -1;
    /*TODO*///		else
    /*TODO*///		{
    /*TODO*///			seq_read_async_start();
    /*TODO*///
    /*TODO*///			sel |= 1 << SEL_BITS;	/* we'll ask for a key */
    /*TODO*///
    /*TODO*///			/* tell updatescreen() to clean after us (in case the window changes size) */
    /*TODO*///			need_to_clear_bitmap = 1;
    /*TODO*///		}
    /*TODO*///	}
    /*TODO*///
    /*TODO*///	if (input_ui_pressed(IPT_UI_CANCEL))
    /*TODO*///		sel = -1;
    /*TODO*///
    /*TODO*///	if (input_ui_pressed(IPT_UI_CONFIGURE))
    /*TODO*///		sel = -2;
    /*TODO*///
    /*TODO*///	if (sel == -1 || sel == -2)
    /*TODO*///	{
    /*TODO*///		/* tell updatescreen() to clean after us */
    /*TODO*///		need_to_clear_bitmap = 1;
    /*TODO*///
    /*TODO*///		record_first_insert = 1;
    /*TODO*///	}
    /*TODO*///
    /*TODO*///	return sel + 1;
    /*TODO*///}
    /*TODO*///
    /*TODO*///
    /*TODO*///static int calibratejoysticks(int selected)
    /*TODO*///{
    /*TODO*///	char *msg;
    /*TODO*///	char buf[2048];
    /*TODO*///	int sel;
    /*TODO*///	static int calibration_started = 0;
    /*TODO*///
    /*TODO*///	sel = selected - 1;
    /*TODO*///
    /*TODO*///	if (calibration_started == 0)
    /*TODO*///	{
    /*TODO*///		osd_joystick_start_calibration();
    /*TODO*///		calibration_started = 1;
    /*TODO*///		strcpy (buf, "");
    /*TODO*///	}
    /*TODO*///
    /*TODO*///	if (sel > SEL_MASK) /* Waiting for the user to acknowledge joystick movement */
    /*TODO*///	{
    /*TODO*///		if (input_ui_pressed(IPT_UI_CANCEL))
    /*TODO*///		{
    /*TODO*///			calibration_started = 0;
    /*TODO*///			sel = -1;
    /*TODO*///		}
    /*TODO*///		else if (input_ui_pressed(IPT_UI_SELECT))
    /*TODO*///		{
    /*TODO*///			osd_joystick_calibrate();
    /*TODO*///			sel &= 0xff;
    /*TODO*///		}
    /*TODO*///
    /*TODO*///		ui_displaymessagewindow(buf);
    /*TODO*///	}
    /*TODO*///	else
    /*TODO*///	{
    /*TODO*///		msg = osd_joystick_calibrate_next();
    /*TODO*///		need_to_clear_bitmap = 1;
    /*TODO*///		if (msg == 0)
    /*TODO*///		{
    /*TODO*///			calibration_started = 0;
    /*TODO*///			osd_joystick_end_calibration();
    /*TODO*///			sel = -1;
    /*TODO*///		}
    /*TODO*///		else
    /*TODO*///		{
    /*TODO*///			strcpy (buf, msg);
    /*TODO*///			ui_displaymessagewindow(buf);
    /*TODO*///			sel |= 1 << SEL_BITS;
    /*TODO*///		}
    /*TODO*///	}
    /*TODO*///
    /*TODO*///	if (input_ui_pressed(IPT_UI_CONFIGURE))
    /*TODO*///		sel = -2;
    /*TODO*///
    /*TODO*///	if (sel == -1 || sel == -2)
    /*TODO*///	{
    /*TODO*///		/* tell updatescreen() to clean after us */
    /*TODO*///		need_to_clear_bitmap = 1;
    /*TODO*///	}
    /*TODO*///
    /*TODO*///	return sel + 1;
    /*TODO*///}
    /*TODO*///
    /*TODO*///
    /*TODO*///static int settraksettings(int selected)
    /*TODO*///{
    /*TODO*///	const char *menu_item[40];
    /*TODO*///	const char *menu_subitem[40];
    /*TODO*///	struct InputPort *entry[40];
    /*TODO*///	int i,sel;
    /*TODO*///	struct InputPort *in;
    /*TODO*///	int total,total2;
    /*TODO*///	int arrowize;
    /*TODO*///
    /*TODO*///
    /*TODO*///	sel = selected - 1;
    /*TODO*///
    /*TODO*///
    /*TODO*///	if (Machine->input_ports == 0)
    /*TODO*///		return 0;
    /*TODO*///
    /*TODO*///	in = Machine->input_ports;
    /*TODO*///
    /*TODO*///	/* Count the total number of analog controls */
    /*TODO*///	total = 0;
    /*TODO*///	while (in->type != IPT_END)
    /*TODO*///	{
    /*TODO*///		if (((in->type & 0xff) > IPT_ANALOG_START) && ((in->type & 0xff) < IPT_ANALOG_END)
    /*TODO*///				&& !(!options.cheat && (in->type & IPF_CHEAT)))
    /*TODO*///		{
    /*TODO*///			entry[total] = in;
    /*TODO*///			total++;
    /*TODO*///		}
    /*TODO*///		in++;
    /*TODO*///	}
    /*TODO*///
    /*TODO*///	if (total == 0) return 0;
    /*TODO*///
    /*TODO*///	/* Each analog control has 3 entries - key & joy delta, reverse, sensitivity */
    /*TODO*///
    /*TODO*///#define ENTRIES 3
    /*TODO*///
    /*TODO*///	total2 = total * ENTRIES;
    /*TODO*///
    /*TODO*///	menu_item[total2] = "Return to Main Menu";
    /*TODO*///	menu_item[total2 + 1] = 0;	/* terminate array */
    /*TODO*///	total2++;
    /*TODO*///
    /*TODO*///	arrowize = 0;
    /*TODO*///	for (i = 0;i < total2;i++)
    /*TODO*///	{
    /*TODO*///		if (i < total2 - 1)
    /*TODO*///		{
    /*TODO*///			char label[30][40];
    /*TODO*///			char setting[30][40];
    /*TODO*///			int sensitivity,delta;
    /*TODO*///			int reverse;
    /*TODO*///
    /*TODO*///			strcpy (label[i], input_port_name(entry[i/ENTRIES]));
    /*TODO*///			sensitivity = IP_GET_SENSITIVITY(entry[i/ENTRIES]);
    /*TODO*///			delta = IP_GET_DELTA(entry[i/ENTRIES]);
    /*TODO*///			reverse = (entry[i/ENTRIES]->type & IPF_REVERSE);
    /*TODO*///
    /*TODO*///			switch (i%ENTRIES)
    /*TODO*///			{
    /*TODO*///				case 0:
    /*TODO*///					strcat (label[i], " Key/Joy Speed");
    /*TODO*///					sprintf(setting[i],"%d",delta);
    /*TODO*///					if (i == sel) arrowize = 3;
    /*TODO*///					break;
    /*TODO*///				case 1:
    /*TODO*///					strcat (label[i], " Reverse");
    /*TODO*///					if (reverse)
    /*TODO*///						sprintf(setting[i],"On");
    /*TODO*///					else
    /*TODO*///						sprintf(setting[i],"Off");
    /*TODO*///					if (i == sel) arrowize = 3;
    /*TODO*///					break;
    /*TODO*///				case 2:
    /*TODO*///					strcat (label[i], " Sensitivity");
    /*TODO*///					sprintf(setting[i],"%3d%%",sensitivity);
    /*TODO*///					if (i == sel) arrowize = 3;
    /*TODO*///					break;
    /*TODO*///			}
    /*TODO*///
    /*TODO*///			menu_item[i] = label[i];
    /*TODO*///			menu_subitem[i] = setting[i];
    /*TODO*///
    /*TODO*///			in++;
    /*TODO*///		}
    /*TODO*///		else menu_subitem[i] = 0;	/* no subitem */
    /*TODO*///	}
    /*TODO*///
    /*TODO*///	ui_displaymenu(menu_item,menu_subitem,0,sel,arrowize);
    /*TODO*///
    /*TODO*///	if (input_ui_pressed_repeat(IPT_UI_DOWN,8))
    /*TODO*///		sel = (sel + 1) % total2;
    /*TODO*///
    /*TODO*///	if (input_ui_pressed_repeat(IPT_UI_UP,8))
    /*TODO*///		sel = (sel + total2 - 1) % total2;
    /*TODO*///
    /*TODO*///	if (input_ui_pressed_repeat(IPT_UI_LEFT,8))
    /*TODO*///	{
    /*TODO*///		if ((sel % ENTRIES) == 0)
    /*TODO*///		/* keyboard/joystick delta */
    /*TODO*///		{
    /*TODO*///			int val = IP_GET_DELTA(entry[sel/ENTRIES]);
    /*TODO*///
    /*TODO*///			val --;
    /*TODO*///			if (val < 1) val = 1;
    /*TODO*///			IP_SET_DELTA(entry[sel/ENTRIES],val);
    /*TODO*///		}
    /*TODO*///		else if ((sel % ENTRIES) == 1)
    /*TODO*///		/* reverse */
    /*TODO*///		{
    /*TODO*///			int reverse = entry[sel/ENTRIES]->type & IPF_REVERSE;
    /*TODO*///			if (reverse)
    /*TODO*///				reverse=0;
    /*TODO*///			else
    /*TODO*///				reverse=IPF_REVERSE;
    /*TODO*///			entry[sel/ENTRIES]->type &= ~IPF_REVERSE;
    /*TODO*///			entry[sel/ENTRIES]->type |= reverse;
    /*TODO*///		}
    /*TODO*///		else if ((sel % ENTRIES) == 2)
    /*TODO*///		/* sensitivity */
    /*TODO*///		{
    /*TODO*///			int val = IP_GET_SENSITIVITY(entry[sel/ENTRIES]);
    /*TODO*///
    /*TODO*///			val --;
    /*TODO*///			if (val < 1) val = 1;
    /*TODO*///			IP_SET_SENSITIVITY(entry[sel/ENTRIES],val);
    /*TODO*///		}
    /*TODO*///	}
    /*TODO*///
    /*TODO*///	if (input_ui_pressed_repeat(IPT_UI_RIGHT,8))
    /*TODO*///	{
    /*TODO*///		if ((sel % ENTRIES) == 0)
    /*TODO*///		/* keyboard/joystick delta */
    /*TODO*///		{
    /*TODO*///			int val = IP_GET_DELTA(entry[sel/ENTRIES]);
    /*TODO*///
    /*TODO*///			val ++;
    /*TODO*///			if (val > 255) val = 255;
    /*TODO*///			IP_SET_DELTA(entry[sel/ENTRIES],val);
    /*TODO*///		}
    /*TODO*///		else if ((sel % ENTRIES) == 1)
    /*TODO*///		/* reverse */
    /*TODO*///		{
    /*TODO*///			int reverse = entry[sel/ENTRIES]->type & IPF_REVERSE;
    /*TODO*///			if (reverse)
    /*TODO*///				reverse=0;
    /*TODO*///			else
    /*TODO*///				reverse=IPF_REVERSE;
    /*TODO*///			entry[sel/ENTRIES]->type &= ~IPF_REVERSE;
    /*TODO*///			entry[sel/ENTRIES]->type |= reverse;
    /*TODO*///		}
    /*TODO*///		else if ((sel % ENTRIES) == 2)
    /*TODO*///		/* sensitivity */
    /*TODO*///		{
    /*TODO*///			int val = IP_GET_SENSITIVITY(entry[sel/ENTRIES]);
    /*TODO*///
    /*TODO*///			val ++;
    /*TODO*///			if (val > 255) val = 255;
    /*TODO*///			IP_SET_SENSITIVITY(entry[sel/ENTRIES],val);
    /*TODO*///		}
    /*TODO*///	}
    /*TODO*///
    /*TODO*///	if (input_ui_pressed(IPT_UI_SELECT))
    /*TODO*///	{
    /*TODO*///		if (sel == total2 - 1) sel = -1;
    /*TODO*///	}
    /*TODO*///
    /*TODO*///	if (input_ui_pressed(IPT_UI_CANCEL))
    /*TODO*///		sel = -1;
    /*TODO*///
    /*TODO*///	if (input_ui_pressed(IPT_UI_CONFIGURE))
    /*TODO*///		sel = -2;
    /*TODO*///
    /*TODO*///	if (sel == -1 || sel == -2)
    /*TODO*///	{
    /*TODO*///		/* tell updatescreen() to clean after us */
    /*TODO*///		need_to_clear_bitmap = 1;
    /*TODO*///	}
    /*TODO*///
    /*TODO*///	return sel + 1;
    /*TODO*///}
    /*TODO*///
    /*TODO*///#ifndef MESS
    /*TODO*///static int mame_stats(int selected)
    /*TODO*///{
    /*TODO*///	char temp[10];
    /*TODO*///	char buf[2048];
    /*TODO*///	int sel, i;
    /*TODO*///
    /*TODO*///
    /*TODO*///	sel = selected - 1;
    /*TODO*///
    /*TODO*///	buf[0] = 0;
    /*TODO*///
    /*TODO*///	if (dispensed_tickets)
    /*TODO*///	{
    /*TODO*///		strcat(buf, "Tickets dispensed: ");
    /*TODO*///		sprintf(temp, "%d\n\n", dispensed_tickets);
    /*TODO*///		strcat(buf, temp);
    /*TODO*///	}
    /*TODO*///
    /*TODO*///	for (i=0;  i<COIN_COUNTERS; i++)
    /*TODO*///	{
    /*TODO*///		sprintf(temp, "Coin %c: ", i+'A');
    /*TODO*///		strcat(buf, temp);
    /*TODO*///		if (!coins[i])
    /*TODO*///			strcat (buf, "NA");
    /*TODO*///		else
    /*TODO*///		{
    /*TODO*///			sprintf (temp, "%d", coins[i]);
    /*TODO*///			strcat (buf, temp);
    /*TODO*///		}
    /*TODO*///		if (coinlockedout[i])
    /*TODO*///		{
    /*TODO*///			strcat(buf, " (locked)\n");
    /*TODO*///		}
    /*TODO*///		else
    /*TODO*///		{
    /*TODO*///			strcat(buf, "\n");
    /*TODO*///		}
    /*TODO*///	}
    /*TODO*///
    /*TODO*///	{
    /*TODO*///		/* menu system, use the normal menu keys */
    /*TODO*///		strcat(buf,"\n\t\x1a Return to Main Menu \x1b");
    /*TODO*///
    /*TODO*///		ui_displaymessagewindow(buf);
    /*TODO*///
    /*TODO*///		if (input_ui_pressed(IPT_UI_SELECT))
    /*TODO*///			sel = -1;
    /*TODO*///
    /*TODO*///		if (input_ui_pressed(IPT_UI_CANCEL))
    /*TODO*///			sel = -1;
    /*TODO*///
    /*TODO*///		if (input_ui_pressed(IPT_UI_CONFIGURE))
    /*TODO*///			sel = -2;
    /*TODO*///	}
    /*TODO*///
    /*TODO*///	if (sel == -1 || sel == -2)
    /*TODO*///	{
    /*TODO*///		/* tell updatescreen() to clean after us */
    /*TODO*///		need_to_clear_bitmap = 1;
    /*TODO*///	}
    /*TODO*///
    /*TODO*///	return sel + 1;
    /*TODO*///}
    /*TODO*///#endif
    /*TODO*///
    public static int showcopyright()
    {
    	int done;
        String buf=sprintf(
    			"Usage of emulators in conjunction with ROMs you don't own " +
    			"is forbidden by copyright law.\n\n" +
    			"IF YOU ARE NOT LEGALLY ENTITLED TO PLAY \"%s\" ON THIS EMULATOR, " +
    			"PRESS ESC.\n\n" +
    			"Otherwise, type OK to continue", 
    			Machine.gamedrv.description);
    	ui_displaymessagewindow(buf);
    
   	setup_selected = -1;////
    	done = 0;
   	do
    	{
    		osd_update_video_and_audio();
    /*TODO*///		osd_poll_joysticks();
    /*TODO*///		if (input_ui_pressed(IPT_UI_CANCEL))
    /*TODO*///		{
    /*TODO*///			setup_selected = 0;////
    /*TODO*///			return 1;
    /*TODO*///		}
                if(keyboard_pressed_memory(KEYCODE_O)!=0)
                {
                    System.out.println("it works!");
                }
    /*TODO*///		if (keyboard_pressed_memory(KEYCODE_O) ||
    /*TODO*///				input_ui_pressed(IPT_UI_LEFT))
    /*TODO*///			done = 1;
    /*TODO*///		if (done == 1 && (keyboard_pressed_memory(KEYCODE_K) ||
    /*TODO*///				input_ui_pressed(IPT_UI_RIGHT)))
    /*TODO*///			done = 2;
    	} while (done < 2);
    
    	setup_selected = 0;////
    	osd_clearbitmap(Machine.scrbitmap);
    	osd_update_video_and_audio();

    	return 0;
    }
    /*TODO*///
    /*TODO*///static int displaygameinfo(int selected)
    /*TODO*///{
    /*TODO*///	int i;
    /*TODO*///	char buf[2048];
    /*TODO*///	int sel;
    /*TODO*///
    /*TODO*///
    /*TODO*///	sel = selected - 1;
    /*TODO*///
    /*TODO*///
    /*TODO*///	sprintf(buf,"%s\n%s %s\n\nCPU:\n",Machine->gamedrv->description,Machine->gamedrv->year,Machine->gamedrv->manufacturer);
    /*TODO*///	i = 0;
    /*TODO*///	while (i < MAX_CPU && Machine->drv->cpu[i].cpu_type)
    /*TODO*///	{
    /*TODO*///		if (Machine->drv->cpu[i].cpu_clock >= 1000000)
    /*TODO*///			sprintf(&buf[strlen(buf)],"%s %d.%06d MHz",
    /*TODO*///					cputype_name(Machine->drv->cpu[i].cpu_type),
    /*TODO*///					Machine->drv->cpu[i].cpu_clock / 1000000,
    /*TODO*///					Machine->drv->cpu[i].cpu_clock % 1000000);
    /*TODO*///		else
    /*TODO*///			sprintf(&buf[strlen(buf)],"%s %d.%03d kHz",
    /*TODO*///					cputype_name(Machine->drv->cpu[i].cpu_type),
    /*TODO*///					Machine->drv->cpu[i].cpu_clock / 1000,
    /*TODO*///					Machine->drv->cpu[i].cpu_clock % 1000);
    /*TODO*///
    /*TODO*///		if (Machine->drv->cpu[i].cpu_type & CPU_AUDIO_CPU)
    /*TODO*///			strcat(buf," (sound)");
    /*TODO*///
    /*TODO*///		strcat(buf,"\n");
    /*TODO*///
    /*TODO*///		i++;
    /*TODO*///	}
    /*TODO*///
    /*TODO*///	strcat(buf,"\nSound");
    /*TODO*///	if (Machine->drv->sound_attributes & SOUND_SUPPORTS_STEREO)
    /*TODO*///		sprintf(&buf[strlen(buf)]," (stereo)");
    /*TODO*///	strcat(buf,":\n");
    /*TODO*///
    /*TODO*///	i = 0;
    /*TODO*///	while (i < MAX_SOUND && Machine->drv->sound[i].sound_type)
    /*TODO*///	{
    /*TODO*///		if (sound_num(&Machine->drv->sound[i]))
    /*TODO*///			sprintf(&buf[strlen(buf)],"%dx",sound_num(&Machine->drv->sound[i]));
    /*TODO*///
    /*TODO*///		sprintf(&buf[strlen(buf)],"%s",sound_name(&Machine->drv->sound[i]));
    /*TODO*///
    /*TODO*///		if (sound_clock(&Machine->drv->sound[i]))
    /*TODO*///		{
    /*TODO*///			if (sound_clock(&Machine->drv->sound[i]) >= 1000000)
    /*TODO*///				sprintf(&buf[strlen(buf)]," %d.%06d MHz",
    /*TODO*///						sound_clock(&Machine->drv->sound[i]) / 1000000,
    /*TODO*///						sound_clock(&Machine->drv->sound[i]) % 1000000);
    /*TODO*///			else
    /*TODO*///				sprintf(&buf[strlen(buf)]," %d.%03d kHz",
    /*TODO*///						sound_clock(&Machine->drv->sound[i]) / 1000,
    /*TODO*///						sound_clock(&Machine->drv->sound[i]) % 1000);
    /*TODO*///		}
    /*TODO*///
    /*TODO*///		strcat(buf,"\n");
    /*TODO*///
    /*TODO*///		i++;
    /*TODO*///	}
    /*TODO*///
    /*TODO*///	if (Machine->drv->video_attributes & VIDEO_TYPE_VECTOR)
    /*TODO*///		sprintf(&buf[strlen(buf)],"\nVector Game\n");
    /*TODO*///	else
    /*TODO*///	{
    /*TODO*///		int pixelx,pixely,tmax,tmin,rem;
    /*TODO*///
    /*TODO*///		pixelx = 4 * (Machine->drv->visible_area.max_y - Machine->drv->visible_area.min_y + 1);
    /*TODO*///		pixely = 3 * (Machine->drv->visible_area.max_x - Machine->drv->visible_area.min_x + 1);
    /*TODO*///
    /*TODO*///		/* calculate MCD */
    /*TODO*///		if (pixelx >= pixely)
    /*TODO*///		{
    /*TODO*///			tmax = pixelx;
    /*TODO*///			tmin = pixely;
    /*TODO*///		}
    /*TODO*///		else
    /*TODO*///		{
    /*TODO*///			tmax = pixely;
    /*TODO*///			tmin = pixelx;
    /*TODO*///		}
    /*TODO*///		while ( (rem = tmax % tmin) )
    /*TODO*///		{
    /*TODO*///			tmax = tmin;
    /*TODO*///			tmin = rem;
    /*TODO*///		}
    /*TODO*///		/* tmin is now the MCD */
    /*TODO*///
    /*TODO*///		pixelx /= tmin;
    /*TODO*///		pixely /= tmin;
    /*TODO*///
    /*TODO*///		sprintf(&buf[strlen(buf)],"\nScreen resolution:\n");
    /*TODO*///		sprintf(&buf[strlen(buf)],"%d x %d (%s) %f Hz\n",
    /*TODO*///				Machine->drv->visible_area.max_x - Machine->drv->visible_area.min_x + 1,
    /*TODO*///				Machine->drv->visible_area.max_y - Machine->drv->visible_area.min_y + 1,
    /*TODO*///				(Machine->gamedrv->flags & ORIENTATION_SWAP_XY) ? "V" : "H",
    /*TODO*///				Machine->drv->frames_per_second);
    /*TODO*///#if 0
    /*TODO*///		sprintf(&buf[strlen(buf)],"pixel aspect ratio %d:%d\n",
    /*TODO*///				pixelx,pixely);
    /*TODO*///		sprintf(&buf[strlen(buf)],"%d colors ",Machine->drv->total_colors);
    /*TODO*///		if (Machine->gamedrv->flags & GAME_REQUIRES_16BIT)
    /*TODO*///			strcat(buf,"(16-bit required)\n");
    /*TODO*///		else if (Machine->drv->video_attributes & VIDEO_MODIFIES_PALETTE)
    /*TODO*///			strcat(buf,"(dynamic)\n");
    /*TODO*///		else strcat(buf,"(static)\n");
    /*TODO*///#endif
    /*TODO*///	}
    /*TODO*///
    /*TODO*///
    /*TODO*///	if (sel == -1)
    /*TODO*///	{
    /*TODO*///		/* startup info, print MAME version and ask for any key */
    /*TODO*///
    /*TODO*///		#ifndef MESS
    /*TODO*///		strcat(buf,"\n\tMAME ");    /* \t means that the line will be centered */
    /*TODO*///		#else
    /*TODO*///		strcat(buf,"\n\tMESS ");    /* \t means that the line will be centered */
    /*TODO*///		#endif
    /*TODO*///
    /*TODO*///		strcat(buf,build_version);
    /*TODO*///		strcat(buf,"\n\tPress any key");
    /*TODO*///		ui_drawbox(0,0,Machine->uiwidth,Machine->uiheight);
    /*TODO*///		ui_displaymessagewindow(buf);
    /*TODO*///
    /*TODO*///		sel = 0;
    /*TODO*///		if (code_read_async() != CODE_NONE)
    /*TODO*///			sel = -1;
    /*TODO*///	}
    /*TODO*///	else
    /*TODO*///	{
    /*TODO*///		/* menu system, use the normal menu keys */
    /*TODO*///		strcat(buf,"\n\t\x1a Return to Main Menu \x1b");
    /*TODO*///
    /*TODO*///		ui_displaymessagewindow(buf);
    /*TODO*///
    /*TODO*///		if (input_ui_pressed(IPT_UI_SELECT))
    /*TODO*///			sel = -1;
    /*TODO*///
    /*TODO*///		if (input_ui_pressed(IPT_UI_CANCEL))
    /*TODO*///			sel = -1;
    /*TODO*///
    /*TODO*///		if (input_ui_pressed(IPT_UI_CONFIGURE))
    /*TODO*///			sel = -2;
    /*TODO*///	}
    /*TODO*///
    /*TODO*///	if (sel == -1 || sel == -2)
    /*TODO*///	{
    /*TODO*///		/* tell updatescreen() to clean after us */
    /*TODO*///		need_to_clear_bitmap = 1;
    /*TODO*///	}
    /*TODO*///
    /*TODO*///	return sel + 1;
    /*TODO*///}
    /*TODO*///
    /*TODO*///
    /*TODO*///int showgamewarnings(void)
    /*TODO*///{
    /*TODO*///	int i;
    /*TODO*///	char buf[2048];
    /*TODO*///
    /*TODO*///	if (Machine->gamedrv->flags &
    /*TODO*///			(GAME_NOT_WORKING | GAME_WRONG_COLORS | GAME_IMPERFECT_COLORS |
    /*TODO*///			  GAME_NO_SOUND | GAME_IMPERFECT_SOUND | GAME_NO_COCKTAIL))
    /*TODO*///	{
    /*TODO*///		int done;
    /*TODO*///
    /*TODO*///		#ifndef MESS
    /*TODO*///		strcpy(buf, "There are known problems with this game:\n\n");
    /*TODO*///		#else
    /*TODO*///		strcpy(buf, "There are known problems with this system:\n\n");
    /*TODO*///		#endif
    /*TODO*///
    /*TODO*///
    /*TODO*///#ifdef MESS
    /*TODO*///		if (Machine->gamedrv->flags & GAME_COMPUTER)
    /*TODO*///		{
    /*TODO*///			strcpy(buf, "The emulated system is a computer: \n\n");
    /*TODO*///			strcat(buf, "The keyboard emulation may not be 100% accurate.\n");
    /*TODO*///		}
    /*TODO*///#endif
    /*TODO*///
    /*TODO*///		if (Machine->gamedrv->flags & GAME_IMPERFECT_COLORS)
    /*TODO*///		{
    /*TODO*///			strcat(buf, "The colors aren't 100% accurate.\n");
    /*TODO*///		}
    /*TODO*///
    /*TODO*///		if (Machine->gamedrv->flags & GAME_WRONG_COLORS)
    /*TODO*///		{
    /*TODO*///			strcat(buf, "The colors are completely wrong.\n");
    /*TODO*///		}
    /*TODO*///
    /*TODO*///		if (Machine->gamedrv->flags & GAME_IMPERFECT_SOUND)
    /*TODO*///		{
    /*TODO*///			strcat(buf, "The sound emulation isn't 100% accurate.\n");
    /*TODO*///		}
    /*TODO*///
    /*TODO*///		if (Machine->gamedrv->flags & GAME_NO_SOUND)
    /*TODO*///		{
    /*TODO*///			strcat(buf, "The game lacks sound.\n");
    /*TODO*///		}
    /*TODO*///
    /*TODO*///		if (Machine->gamedrv->flags & GAME_NO_COCKTAIL)
    /*TODO*///		{
    /*TODO*///			strcat(buf, "Screen flipping in cocktail mode is not supported.\n");
    /*TODO*///		}
    /*TODO*///
    /*TODO*///		if (Machine->gamedrv->flags & GAME_NOT_WORKING)
    /*TODO*///		{
    /*TODO*///			const struct GameDriver *maindrv;
    /*TODO*///			int foundworking;
    /*TODO*///
    /*TODO*///			#ifdef MESS
    /*TODO*///			strcpy(buf,"THIS SYSTEM DOESN'T WORK PROPERLY");
    /*TODO*///			#else
    /*TODO*///			strcpy(buf,"THIS GAME DOESN'T WORK PROPERLY");
    /*TODO*///			#endif
    /*TODO*///
    /*TODO*///			if (Machine->gamedrv->clone_of && !(Machine->gamedrv->clone_of->flags & NOT_A_DRIVER))
    /*TODO*///				maindrv = Machine->gamedrv->clone_of;
    /*TODO*///			else maindrv = Machine->gamedrv;
    /*TODO*///
    /*TODO*///			foundworking = 0;
    /*TODO*///			i = 0;
    /*TODO*///			while (drivers[i])
    /*TODO*///			{
    /*TODO*///				if (drivers[i] == maindrv || drivers[i]->clone_of == maindrv)
    /*TODO*///				{
    /*TODO*///					if ((drivers[i]->flags & GAME_NOT_WORKING) == 0)
    /*TODO*///					{
    /*TODO*///						if (foundworking == 0)
    /*TODO*///							strcat(buf,"\n\nThere are working clones of this game. They are:\n\n");
    /*TODO*///						foundworking = 1;
    /*TODO*///
    /*TODO*///						sprintf(&buf[strlen(buf)],"%s\n",drivers[i]->name);
    /*TODO*///					}
    /*TODO*///				}
    /*TODO*///				i++;
    /*TODO*///			}
    /*TODO*///		}
    /*TODO*///
    /*TODO*///		strcat(buf,"\n\nType OK to continue");
    /*TODO*///
    /*TODO*///		ui_displaymessagewindow(buf);
    /*TODO*///
    /*TODO*///		done = 0;
    /*TODO*///		do
    /*TODO*///		{
    /*TODO*///			osd_update_video_and_audio();
    /*TODO*///			osd_poll_joysticks();
    /*TODO*///			if (input_ui_pressed(IPT_UI_CANCEL))
    /*TODO*///				return 1;
    /*TODO*///			if (code_pressed_memory(KEYCODE_O) ||
    /*TODO*///					input_ui_pressed(IPT_UI_LEFT))
    /*TODO*///				done = 1;
    /*TODO*///			if (done == 1 && (code_pressed_memory(KEYCODE_K) ||
    /*TODO*///					input_ui_pressed(IPT_UI_RIGHT)))
    /*TODO*///				done = 2;
    /*TODO*///		} while (done < 2);
    /*TODO*///	}
    /*TODO*///
    /*TODO*///
    /*TODO*///	osd_clearbitmap(Machine->scrbitmap);
    /*TODO*///
    /*TODO*///	/* clear the input memory */
    /*TODO*///	while (code_read_async() != CODE_NONE);
    /*TODO*///
    /*TODO*///	while (displaygameinfo(0) == 1)
    /*TODO*///	{
    /*TODO*///		osd_update_video_and_audio();
    /*TODO*///		osd_poll_joysticks();
    /*TODO*///	}
    /*TODO*///
    /*TODO*///	#ifdef MESS
    /*TODO*///	while (displayimageinfo(0) == 1)
    /*TODO*///	{
    /*TODO*///		osd_update_video_and_audio();
    /*TODO*///		osd_poll_joysticks();
    /*TODO*///	}
    /*TODO*///	#endif
    /*TODO*///
    /*TODO*///	osd_clearbitmap(Machine->scrbitmap);
    /*TODO*///	/* make sure that the screen is really cleared, in case autoframeskip kicked in */
    /*TODO*///	osd_update_video_and_audio();
    /*TODO*///	osd_update_video_and_audio();
    /*TODO*///	osd_update_video_and_audio();
    /*TODO*///	osd_update_video_and_audio();
    /*TODO*///
    /*TODO*///	return 0;
    /*TODO*///}
    /*TODO*///
    /*TODO*////* Word-wraps the text in the specified buffer to fit in maxwidth characters per line.
    /*TODO*///   The contents of the buffer are modified.
    /*TODO*///   Known limitations: Words longer than maxwidth cause the function to fail. */
    /*TODO*///static void wordwrap_text_buffer (char *buffer, int maxwidth)
    /*TODO*///{
    /*TODO*///	int width = 0;
    /*TODO*///
    /*TODO*///	while (*buffer)
    /*TODO*///	{
    /*TODO*///		if (*buffer == '\n')
    /*TODO*///		{
    /*TODO*///			buffer++;
    /*TODO*///			width = 0;
    /*TODO*///			continue;
    /*TODO*///		}
    /*TODO*///
    /*TODO*///		width++;
    /*TODO*///
    /*TODO*///		if (width > maxwidth)
    /*TODO*///		{
    /*TODO*///			/* backtrack until a space is found */
    /*TODO*///			while (*buffer != ' ')
    /*TODO*///			{
    /*TODO*///				buffer--;
    /*TODO*///				width--;
    /*TODO*///			}
    /*TODO*///			if (width < 1) return;	/* word too long */
    /*TODO*///
    /*TODO*///			/* replace space with a newline */
    /*TODO*///			*buffer = '\n';
    /*TODO*///		}
    /*TODO*///		else
    /*TODO*///			buffer++;
    /*TODO*///	}
    /*TODO*///}
    /*TODO*///
    /*TODO*///static int count_lines_in_buffer (char *buffer)
    /*TODO*///{
    /*TODO*///	int lines = 0;
    /*TODO*///	char c;
    /*TODO*///
    /*TODO*///	while ( (c = *buffer++) )
    /*TODO*///		if (c == '\n') lines++;
    /*TODO*///
    /*TODO*///	return lines;
    /*TODO*///}
    /*TODO*///
    /*TODO*////* Display lines from buffer, starting with line 'scroll', in a width x height text window */
    /*TODO*///static void display_scroll_message (int *scroll, int width, int height, char *buf)
    /*TODO*///{
    /*TODO*///	struct DisplayText dt[256];
    /*TODO*///	int curr_dt = 0;
    /*TODO*///	char uparrow[2] = "\x18";
    /*TODO*///	char downarrow[2] = "\x19";
    /*TODO*///	char textcopy[2048];
    /*TODO*///	char *copy;
    /*TODO*///	int leftoffs,topoffs;
    /*TODO*///	int first = *scroll;
    /*TODO*///	int buflines,showlines;
    /*TODO*///	int i;
    /*TODO*///
    /*TODO*///
    /*TODO*///	/* draw box */
    /*TODO*///	leftoffs = (Machine->uiwidth - Machine->uifontwidth * (width + 1)) / 2;
    /*TODO*///	if (leftoffs < 0) leftoffs = 0;
    /*TODO*///	topoffs = (Machine->uiheight - (3 * height + 1) * Machine->uifontheight / 2) / 2;
    /*TODO*///	ui_drawbox(leftoffs,topoffs,(width + 1) * Machine->uifontwidth,(3 * height + 1) * Machine->uifontheight / 2);
    /*TODO*///
    /*TODO*///	buflines = count_lines_in_buffer (buf);
    /*TODO*///	if (first > 0)
    /*TODO*///	{
    /*TODO*///		if (buflines <= height)
    /*TODO*///			first = 0;
    /*TODO*///		else
    /*TODO*///		{
    /*TODO*///			height--;
    /*TODO*///			if (first > (buflines - height))
    /*TODO*///				first = buflines - height;
    /*TODO*///		}
    /*TODO*///		*scroll = first;
    /*TODO*///	}
    /*TODO*///
    /*TODO*///	if (first != 0)
    /*TODO*///	{
    /*TODO*///		/* indicate that scrolling upward is possible */
    /*TODO*///		dt[curr_dt].text = uparrow;
    /*TODO*///		dt[curr_dt].color = DT_COLOR_WHITE;
    /*TODO*///		dt[curr_dt].x = (Machine->uiwidth - Machine->uifontwidth * strlen(uparrow)) / 2;
    /*TODO*///		dt[curr_dt].y = topoffs + (3*curr_dt+1)*Machine->uifontheight/2;
    /*TODO*///		curr_dt++;
    /*TODO*///	}
    /*TODO*///
    /*TODO*///	if ((buflines - first) > height)
    /*TODO*///		showlines = height - 1;
    /*TODO*///	else
    /*TODO*///		showlines = height;
    /*TODO*///
    /*TODO*///	/* skip to first line */
    /*TODO*///	while (first > 0)
    /*TODO*///	{
    /*TODO*///		char c;
    /*TODO*///
    /*TODO*///		while ( (c = *buf++) )
    /*TODO*///		{
    /*TODO*///			if (c == '\n')
    /*TODO*///			{
    /*TODO*///				first--;
    /*TODO*///				break;
    /*TODO*///			}
    /*TODO*///		}
    /*TODO*///	}
    /*TODO*///
    /*TODO*///	/* copy 'showlines' lines from buffer, starting with line 'first' */
    /*TODO*///	copy = textcopy;
    /*TODO*///	for (i = 0; i < showlines; i++)
    /*TODO*///	{
    /*TODO*///		char *copystart = copy;
    /*TODO*///
    /*TODO*///		while (*buf && *buf != '\n')
    /*TODO*///		{
    /*TODO*///			*copy = *buf;
    /*TODO*///			copy++;
    /*TODO*///			buf++;
    /*TODO*///		}
    /*TODO*///		*copy = '\0';
    /*TODO*///		copy++;
    /*TODO*///		if (*buf == '\n')
    /*TODO*///			buf++;
    /*TODO*///
    /*TODO*///		if (*copystart == '\t') /* center text */
    /*TODO*///		{
    /*TODO*///			copystart++;
    /*TODO*///			dt[curr_dt].x = (Machine->uiwidth - Machine->uifontwidth * (copy - copystart)) / 2;
    /*TODO*///		}
    /*TODO*///		else
    /*TODO*///			dt[curr_dt].x = leftoffs + Machine->uifontwidth/2;
    /*TODO*///
    /*TODO*///		dt[curr_dt].text = copystart;
    /*TODO*///		dt[curr_dt].color = DT_COLOR_WHITE;
    /*TODO*///		dt[curr_dt].y = topoffs + (3*curr_dt+1)*Machine->uifontheight/2;
    /*TODO*///		curr_dt++;
    /*TODO*///	}
    /*TODO*///
    /*TODO*///	if (showlines == (height - 1))
    /*TODO*///	{
    /*TODO*///		/* indicate that scrolling downward is possible */
    /*TODO*///		dt[curr_dt].text = downarrow;
    /*TODO*///		dt[curr_dt].color = DT_COLOR_WHITE;
    /*TODO*///		dt[curr_dt].x = (Machine->uiwidth - Machine->uifontwidth * strlen(downarrow)) / 2;
    /*TODO*///		dt[curr_dt].y = topoffs + (3*curr_dt+1)*Machine->uifontheight/2;
    /*TODO*///		curr_dt++;
    /*TODO*///	}
    /*TODO*///
    /*TODO*///	dt[curr_dt].text = 0;	/* terminate array */
    /*TODO*///
    /*TODO*///	displaytext(dt,0,0);
    /*TODO*///}
    /*TODO*///
    /*TODO*///
    /*TODO*////* Display text entry for current driver from history.dat and mameinfo.dat. */
    /*TODO*///static int displayhistory (int selected)
    /*TODO*///{
    /*TODO*///	#ifndef MESS
    /*TODO*///	char *msg = "\tHistory not available\n\n\t\x1a Return to Main Menu \x1b";
    /*TODO*///	#else
    /*TODO*///	char *msg = "\tSysInfo.dat Missing\n\n\t\x1a Return to Main Menu \x1b";
    /*TODO*///	#endif
    /*TODO*///	static int scroll = 0;
    /*TODO*///	static char *buf = 0;
    /*TODO*///	int maxcols,maxrows;
    /*TODO*///	int sel;
    /*TODO*///
    /*TODO*///
    /*TODO*///	sel = selected - 1;
    /*TODO*///
    /*TODO*///
    /*TODO*///	maxcols = (Machine->uiwidth / Machine->uifontwidth) - 1;
    /*TODO*///	maxrows = (2 * Machine->uiheight - Machine->uifontheight) / (3 * Machine->uifontheight);
    /*TODO*///	maxcols -= 2;
    /*TODO*///	maxrows -= 8;
    /*TODO*///
    /*TODO*///	if (!buf)
    /*TODO*///	{
    /*TODO*///		/* allocate a buffer for the text */
    /*TODO*///		buf = malloc (8192);
    /*TODO*///		if (buf)
    /*TODO*///		{
    /*TODO*///			/* try to load entry */
    /*TODO*///			if (load_driver_history (Machine->gamedrv, buf, 8192) == 0)
    /*TODO*///			{
    /*TODO*///				scroll = 0;
    /*TODO*///				wordwrap_text_buffer (buf, maxcols);
    /*TODO*///				strcat(buf,"\n\t\x1a Return to Main Menu \x1b\n");
    /*TODO*///			}
    /*TODO*///			else
    /*TODO*///			{
    /*TODO*///				free (buf);
    /*TODO*///				buf = 0;
    /*TODO*///			}
    /*TODO*///		}
    /*TODO*///	}
    /*TODO*///
    /*TODO*///	{
    /*TODO*///		if (buf)
    /*TODO*///			display_scroll_message (&scroll, maxcols, maxrows, buf);
    /*TODO*///		else
    /*TODO*///			ui_displaymessagewindow (msg);
    /*TODO*///
    /*TODO*///		if ((scroll > 0) && input_ui_pressed_repeat(IPT_UI_UP,4))
    /*TODO*///		{
    /*TODO*///			if (scroll == 2) scroll = 0;	/* 1 would be the same as 0, but with arrow on top */
    /*TODO*///			else scroll--;
    /*TODO*///		}
    /*TODO*///
    /*TODO*///		if (input_ui_pressed_repeat(IPT_UI_DOWN,4))
    /*TODO*///		{
    /*TODO*///			if (scroll == 0) scroll = 2;	/* 1 would be the same as 0, but with arrow on top */
    /*TODO*///			else scroll++;
    /*TODO*///		}
    /*TODO*///
    /*TODO*///		if (input_ui_pressed(IPT_UI_SELECT))
    /*TODO*///			sel = -1;
    /*TODO*///
    /*TODO*///		if (input_ui_pressed(IPT_UI_CANCEL))
    /*TODO*///			sel = -1;
    /*TODO*///
    /*TODO*///		if (input_ui_pressed(IPT_UI_CONFIGURE))
    /*TODO*///			sel = -2;
    /*TODO*///	}
    /*TODO*///
    /*TODO*///	if (sel == -1 || sel == -2)
    /*TODO*///	{
    /*TODO*///		/* tell updatescreen() to clean after us */
    /*TODO*///		need_to_clear_bitmap = 1;
    /*TODO*///
    /*TODO*///		/* force buffer to be recreated */
    /*TODO*///		if (buf)
    /*TODO*///		{
    /*TODO*///			free (buf);
    /*TODO*///			buf = 0;
    /*TODO*///		}
    /*TODO*///	}
    /*TODO*///
    /*TODO*///	return sel + 1;
    /*TODO*///
    /*TODO*///}
    /*TODO*///
    /*TODO*///
    /*TODO*///#ifndef NEOFREE
    /*TODO*///#ifndef TINY_COMPILE
    /*TODO*///int memcard_menu(int selection)
    /*TODO*///{
    /*TODO*///	int sel;
    /*TODO*///	int menutotal = 0;
    /*TODO*///	const char *menuitem[10];
    /*TODO*///	char	buffer[300];
    /*TODO*///	char	*msg;
    /*TODO*///
    /*TODO*///	sel = selection - 1 ;
    /*TODO*///
    /*TODO*///	sprintf(buffer, "Load Memory Card %03d", mcd_number);
    /*TODO*///	menuitem[menutotal++] = buffer;
    /*TODO*///	menuitem[menutotal++] = "Eject Memory Card";
    /*TODO*///	menuitem[menutotal++] = "Create Memory Card";
    /*TODO*///	menuitem[menutotal++] = "Call Memory Card Manager (RESET)";
    /*TODO*///	menuitem[menutotal++] = "Return to Main Menu";
    /*TODO*///	menuitem[menutotal] = 0;
    /*TODO*///
    /*TODO*///	if (mcd_action!=0)
    /*TODO*///	{
    /*TODO*///		switch(mcd_action)
    /*TODO*///		{
    /*TODO*///		case	1:
    /*TODO*///			msg = "\nFailed To Load Memory Card!\n\n";
    /*TODO*///			break;
    /*TODO*///		case	2:
    /*TODO*///			msg = "\nLoad OK!\n\n";
    /*TODO*///			break;
    /*TODO*///		case	3:
    /*TODO*///			msg = "\nMemory Card Ejected!\n\n";
    /*TODO*///			break;
    /*TODO*///		case	4:
    /*TODO*///			msg = "\nMemory Card Created OK!\n\n";
    /*TODO*///			break;
    /*TODO*///		case	5:
    /*TODO*///			msg = "\nFailed To Create Memory Card!\n(It already exists ?)\n\n";
    /*TODO*///			break;
    /*TODO*///		default:
    /*TODO*///			msg = "\nDAMN!! Internal Error!\n\n";
    /*TODO*///		}
    /*TODO*///		ui_displaymessagewindow(msg);
    /*TODO*///		if (input_ui_pressed(IPT_UI_SELECT))
    /*TODO*///			mcd_action = 0;
    /*TODO*///	}
    /*TODO*///	else
    /*TODO*///	{
    /*TODO*///		ui_displaymenu(menuitem,0,0,sel,0);
    /*TODO*///
    /*TODO*///		if (input_ui_pressed_repeat(IPT_UI_RIGHT,8))
    /*TODO*///			mcd_number = (mcd_number + 1) % 1000;
    /*TODO*///
    /*TODO*///		if (input_ui_pressed_repeat(IPT_UI_LEFT,8))
    /*TODO*///			mcd_number = (mcd_number + 999) % 1000;
    /*TODO*///
    /*TODO*///		if (input_ui_pressed_repeat(IPT_UI_DOWN,8))
    /*TODO*///			sel = (sel + 1) % menutotal;
    /*TODO*///
    /*TODO*///		if (input_ui_pressed_repeat(IPT_UI_UP,8))
    /*TODO*///			sel = (sel + menutotal - 1) % menutotal;
    /*TODO*///
    /*TODO*///		if (input_ui_pressed(IPT_UI_SELECT))
    /*TODO*///		{
    /*TODO*///			switch(sel)
    /*TODO*///			{
    /*TODO*///			case 0:
    /*TODO*///				neogeo_memcard_eject();
    /*TODO*///				if (neogeo_memcard_load(mcd_number))
    /*TODO*///				{
    /*TODO*///					memcard_status=1;
    /*TODO*///					memcard_number=mcd_number;
    /*TODO*///					mcd_action = 2;
    /*TODO*///				}
    /*TODO*///				else
    /*TODO*///					mcd_action = 1;
    /*TODO*///				break;
    /*TODO*///			case 1:
    /*TODO*///				neogeo_memcard_eject();
    /*TODO*///				mcd_action = 3;
    /*TODO*///				break;
    /*TODO*///			case 2:
    /*TODO*///				if (neogeo_memcard_create(mcd_number))
    /*TODO*///					mcd_action = 4;
    /*TODO*///				else
    /*TODO*///					mcd_action = 5;
    /*TODO*///				break;
    /*TODO*///			case 3:
    /*TODO*///				memcard_manager=1;
    /*TODO*///				sel=-2;
    /*TODO*///				machine_reset();
    /*TODO*///				break;
    /*TODO*///			case 4:
    /*TODO*///				sel=-1;
    /*TODO*///				break;
    /*TODO*///			}
    /*TODO*///		}
    /*TODO*///
    /*TODO*///		if (input_ui_pressed(IPT_UI_CANCEL))
    /*TODO*///			sel = -1;
    /*TODO*///
    /*TODO*///		if (input_ui_pressed(IPT_UI_CONFIGURE))
    /*TODO*///			sel = -2;
    /*TODO*///
    /*TODO*///		if (sel == -1 || sel == -2)
    /*TODO*///		{
    /*TODO*///			/* tell updatescreen() to clean after us */
    /*TODO*///			need_to_clear_bitmap = 1;
    /*TODO*///		}
    /*TODO*///	}
    /*TODO*///
    /*TODO*///	return sel + 1;
    /*TODO*///}
    /*TODO*///#endif
    /*TODO*///#endif
    /*TODO*///
    /*TODO*///
    /*TODO*///#ifndef MESS
    /*TODO*///enum { UI_SWITCH = 0,UI_DEFCODE,UI_CODE,UI_ANALOG,UI_CALIBRATE,
    /*TODO*///		UI_STATS,UI_GAMEINFO, UI_HISTORY,
    /*TODO*///		UI_CHEAT,UI_RESET,UI_MEMCARD,UI_EXIT };
    /*TODO*///#else
    /*TODO*///enum { UI_SWITCH = 0,UI_DEFCODE,UI_CODE,UI_ANALOG,UI_CALIBRATE,
    /*TODO*///		UI_GAMEINFO, UI_IMAGEINFO,UI_FILEMANAGER,UI_TAPECONTROL,
    /*TODO*///		UI_HISTORY,UI_CHEAT,UI_RESET,UI_MEMCARD,UI_EXIT };
    /*TODO*///#endif
    /*TODO*///
    /*TODO*///
    /*TODO*///#define MAX_SETUPMENU_ITEMS 20
    /*TODO*///static const char *menu_item[MAX_SETUPMENU_ITEMS];
    /*TODO*///static int menu_action[MAX_SETUPMENU_ITEMS];
    /*TODO*///static int menu_total;
    /*TODO*///
    /*TODO*///
    /*TODO*///static void setup_menu_init(void)
    /*TODO*///{
    /*TODO*///	menu_total = 0;
    /*TODO*///
    /*TODO*///	menu_item[menu_total] = "Input (general)"; menu_action[menu_total++] = UI_DEFCODE;
    /*TODO*///	#ifndef MESS
    /*TODO*///	menu_item[menu_total] = "Input (this game)"; menu_action[menu_total++] = UI_CODE;
    /*TODO*///	#else
    /*TODO*///	menu_item[menu_total] = "Input (this machine)"; menu_action[menu_total++] = UI_CODE;
    /*TODO*///	#endif
    /*TODO*///	menu_item[menu_total] = "Dip Switches"; menu_action[menu_total++] = UI_SWITCH;
    /*TODO*///
    /*TODO*///	/* Determine if there are any analog controls */
    /*TODO*///	{
    /*TODO*///		struct InputPort *in;
    /*TODO*///		int num;
    /*TODO*///
    /*TODO*///		in = Machine->input_ports;
    /*TODO*///
    /*TODO*///		num = 0;
    /*TODO*///		while (in->type != IPT_END)
    /*TODO*///		{
    /*TODO*///			if (((in->type & 0xff) > IPT_ANALOG_START) && ((in->type & 0xff) < IPT_ANALOG_END)
    /*TODO*///					&& !(!options.cheat && (in->type & IPF_CHEAT)))
    /*TODO*///				num++;
    /*TODO*///			in++;
    /*TODO*///		}
    /*TODO*///
    /*TODO*///		if (num != 0)
    /*TODO*///		{
    /*TODO*///			menu_item[menu_total] = "Analog Controls"; menu_action[menu_total++] = UI_ANALOG;
    /*TODO*///		}
    /*TODO*///	}
    /*TODO*///
    /*TODO*///	/* Joystick calibration possible? */
    /*TODO*///	if ((osd_joystick_needs_calibration()) != 0)
    /*TODO*///	{
    /*TODO*///		menu_item[menu_total] = "Calibrate Joysticks"; menu_action[menu_total++] = UI_CALIBRATE;
    /*TODO*///	}
    /*TODO*///
    /*TODO*///	#ifndef MESS
    /*TODO*///	menu_item[menu_total] = "Bookkeeping Info"; menu_action[menu_total++] = UI_STATS;
    /*TODO*///	menu_item[menu_total] = "Game Information"; menu_action[menu_total++] = UI_GAMEINFO;
    /*TODO*///	menu_item[menu_total] = "Game History"; menu_action[menu_total++] = UI_HISTORY;
    /*TODO*///	#else
    /*TODO*///	menu_item[menu_total] = "Machine Information"; menu_action[menu_total++] = UI_GAMEINFO;
    /*TODO*///	menu_item[menu_total] = "Image Information"; menu_action[menu_total++] = UI_IMAGEINFO;
    /*TODO*///	menu_item[menu_total] = "File Manager"; menu_action[menu_total++] = UI_FILEMANAGER;
    /*TODO*///	menu_item[menu_total] = "Tape Control"; menu_action[menu_total++] = UI_TAPECONTROL;
    /*TODO*///	menu_item[menu_total] = "Machine Usage & History"; menu_action[menu_total++] = UI_HISTORY;
    /*TODO*///	#endif
    /*TODO*///
    /*TODO*///	if (options.cheat)
    /*TODO*///	{
    /*TODO*///		menu_item[menu_total] = "Cheat"; menu_action[menu_total++] = UI_CHEAT;
    /*TODO*///	}
    /*TODO*///
    /*TODO*///#ifndef NEOFREE
    /*TODO*///#ifndef TINY_COMPILE
    /*TODO*///	if (Machine->gamedrv->clone_of == &driver_neogeo ||
    /*TODO*///			(Machine->gamedrv->clone_of &&
    /*TODO*///				Machine->gamedrv->clone_of->clone_of == &driver_neogeo))
    /*TODO*///	{
    /*TODO*///		menu_item[menu_total] = "Memory Card"; menu_action[menu_total++] = UI_MEMCARD;
    /*TODO*///	}
    /*TODO*///#endif
    /*TODO*///#endif
    /*TODO*///
    /*TODO*///	#ifndef MESS
    /*TODO*///	menu_item[menu_total] = "Reset Game"; menu_action[menu_total++] = UI_RESET;
    /*TODO*///	menu_item[menu_total] = "Return to Game"; menu_action[menu_total++] = UI_EXIT;
    /*TODO*///	#else
    /*TODO*///	menu_item[menu_total] = "Reset Machine"; menu_action[menu_total++] = UI_RESET;
    /*TODO*///	menu_item[menu_total] = "Return to Machine"; menu_action[menu_total++] = UI_EXIT;
    /*TODO*///	#endif
    /*TODO*///	menu_item[menu_total] = 0; /* terminate array */
    /*TODO*///}
    /*TODO*///
    /*TODO*///
    /*TODO*///static int setup_menu(int selected)
    /*TODO*///{
    /*TODO*///	int sel,res;
    /*TODO*///	static int menu_lastselected = 0;
    /*TODO*///
    /*TODO*///
    /*TODO*///	if (selected == -1)
    /*TODO*///		sel = menu_lastselected;
    /*TODO*///	else sel = selected - 1;
    /*TODO*///
    /*TODO*///	if (sel > SEL_MASK)
    /*TODO*///	{
    /*TODO*///		switch (menu_action[sel & SEL_MASK])
    /*TODO*///		{
    /*TODO*///			case UI_SWITCH:
    /*TODO*///				res = setdipswitches(sel >> SEL_BITS);
    /*TODO*///				if (res == -1)
    /*TODO*///				{
    /*TODO*///					menu_lastselected = sel;
    /*TODO*///					sel = -1;
    /*TODO*///				}
    /*TODO*///				else
    /*TODO*///					sel = (sel & SEL_MASK) | (res << SEL_BITS);
    /*TODO*///				break;
    /*TODO*///
    /*TODO*///			case UI_DEFCODE:
    /*TODO*///				res = setdefcodesettings(sel >> SEL_BITS);
    /*TODO*///				if (res == -1)
    /*TODO*///				{
    /*TODO*///					menu_lastselected = sel;
    /*TODO*///					sel = -1;
    /*TODO*///				}
    /*TODO*///				else
    /*TODO*///					sel = (sel & SEL_MASK) | (res << SEL_BITS);
    /*TODO*///				break;
    /*TODO*///
    /*TODO*///			case UI_CODE:
    /*TODO*///				res = setcodesettings(sel >> SEL_BITS);
    /*TODO*///				if (res == -1)
    /*TODO*///				{
    /*TODO*///					menu_lastselected = sel;
    /*TODO*///					sel = -1;
    /*TODO*///				}
    /*TODO*///				else
    /*TODO*///					sel = (sel & SEL_MASK) | (res << SEL_BITS);
    /*TODO*///				break;
    /*TODO*///
    /*TODO*///			case UI_ANALOG:
    /*TODO*///				res = settraksettings(sel >> SEL_BITS);
    /*TODO*///				if (res == -1)
    /*TODO*///				{
    /*TODO*///					menu_lastselected = sel;
    /*TODO*///					sel = -1;
    /*TODO*///				}
    /*TODO*///				else
    /*TODO*///					sel = (sel & SEL_MASK) | (res << SEL_BITS);
    /*TODO*///				break;
    /*TODO*///
    /*TODO*///			case UI_CALIBRATE:
    /*TODO*///				res = calibratejoysticks(sel >> SEL_BITS);
    /*TODO*///				if (res == -1)
    /*TODO*///				{
    /*TODO*///					menu_lastselected = sel;
    /*TODO*///					sel = -1;
    /*TODO*///				}
    /*TODO*///				else
    /*TODO*///					sel = (sel & SEL_MASK) | (res << SEL_BITS);
    /*TODO*///				break;
    /*TODO*///
    /*TODO*///
    /*TODO*///			#ifndef MESS
    /*TODO*///			case UI_STATS:
    /*TODO*///				res = mame_stats(sel >> SEL_BITS);
    /*TODO*///				if (res == -1)
    /*TODO*///				{
    /*TODO*///					menu_lastselected = sel;
    /*TODO*///					sel = -1;
    /*TODO*///				}
    /*TODO*///				else
    /*TODO*///					sel = (sel & SEL_MASK) | (res << SEL_BITS);
    /*TODO*///				break;
    /*TODO*///			#endif
    /*TODO*///
    /*TODO*///			case UI_GAMEINFO:
    /*TODO*///				res = displaygameinfo(sel >> SEL_BITS);
    /*TODO*///				if (res == -1)
    /*TODO*///				{
    /*TODO*///					menu_lastselected = sel;
    /*TODO*///					sel = -1;
    /*TODO*///				}
    /*TODO*///				else
    /*TODO*///					sel = (sel & SEL_MASK) | (res << SEL_BITS);
    /*TODO*///				break;
    /*TODO*///
    /*TODO*///			#ifdef MESS
    /*TODO*///			case UI_IMAGEINFO:
    /*TODO*///				res = displayimageinfo(sel >> SEL_BITS);
    /*TODO*///				if (res == -1)
    /*TODO*///				{
    /*TODO*///					menu_lastselected = sel;
    /*TODO*///					sel = -1;
    /*TODO*///				}
    /*TODO*///				else
    /*TODO*///					sel = (sel & SEL_MASK) | (res << SEL_BITS);
    /*TODO*///				break;
    /*TODO*///			case UI_FILEMANAGER:
    /*TODO*///				res = filemanager(sel >> SEL_BITS);
    /*TODO*///				if (res == -1)
    /*TODO*///				{
    /*TODO*///					menu_lastselected = sel;
    /*TODO*///					sel = -1;
    /*TODO*///				}
    /*TODO*///				else
    /*TODO*///					sel = (sel & SEL_MASK) | (res << SEL_BITS);
    /*TODO*///				break;
    /*TODO*///			case UI_TAPECONTROL:
    /*TODO*///				res = tapecontrol(sel >> SEL_BITS);
    /*TODO*///				if (res == -1)
    /*TODO*///				{
    /*TODO*///					menu_lastselected = sel;
    /*TODO*///					sel = -1;
    /*TODO*///				}
    /*TODO*///				else
    /*TODO*///					sel = (sel & SEL_MASK) | (res << SEL_BITS);
    /*TODO*///				break;
    /*TODO*///			#endif
    /*TODO*///
    /*TODO*///			case UI_HISTORY:
    /*TODO*///				res = displayhistory(sel >> SEL_BITS);
    /*TODO*///				if (res == -1)
    /*TODO*///				{
    /*TODO*///					menu_lastselected = sel;
    /*TODO*///					sel = -1;
    /*TODO*///				}
    /*TODO*///				else
    /*TODO*///					sel = (sel & SEL_MASK) | (res << SEL_BITS);
    /*TODO*///				break;
    /*TODO*///
    /*TODO*///			case UI_CHEAT:
    /*TODO*///osd_sound_enable(0);
    /*TODO*///while (seq_pressed(input_port_type_seq(IPT_UI_SELECT)))
    /*TODO*///	osd_update_video_and_audio();	  /* give time to the sound hardware to apply the volume change */
    /*TODO*///				cheat_menu();
    /*TODO*///osd_sound_enable(1);
    /*TODO*///sel = sel & SEL_MASK;
    /*TODO*///				break;
    /*TODO*///
    /*TODO*///#ifndef NEOFREE
    /*TODO*///#ifndef TINY_COMPILE
    /*TODO*///			case UI_MEMCARD:
    /*TODO*///				res = memcard_menu(sel >> SEL_BITS);
    /*TODO*///				if (res == -1)
    /*TODO*///				{
    /*TODO*///					menu_lastselected = sel;
    /*TODO*///					sel = -1;
    /*TODO*///				}
    /*TODO*///				else
    /*TODO*///					sel = (sel & SEL_MASK) | (res << SEL_BITS);
    /*TODO*///				break;
    /*TODO*///#endif
    /*TODO*///#endif
    /*TODO*///		}
    /*TODO*///
    /*TODO*///		return sel + 1;
    /*TODO*///	}
    /*TODO*///
    /*TODO*///
    /*TODO*///	ui_displaymenu(menu_item,0,0,sel,0);
    /*TODO*///
    /*TODO*///	if (input_ui_pressed_repeat(IPT_UI_DOWN,8))
    /*TODO*///		sel = (sel + 1) % menu_total;
    /*TODO*///
    /*TODO*///	if (input_ui_pressed_repeat(IPT_UI_UP,8))
    /*TODO*///		sel = (sel + menu_total - 1) % menu_total;
    /*TODO*///
    /*TODO*///	if (input_ui_pressed(IPT_UI_SELECT))
    /*TODO*///	{
    /*TODO*///		switch (menu_action[sel])
    /*TODO*///		{
    /*TODO*///			case UI_SWITCH:
    /*TODO*///			case UI_DEFCODE:
    /*TODO*///			case UI_CODE:
    /*TODO*///			case UI_ANALOG:
    /*TODO*///			case UI_CALIBRATE:
    /*TODO*///			#ifndef MESS
    /*TODO*///			case UI_STATS:
    /*TODO*///			case UI_GAMEINFO:
    /*TODO*///			#else
    /*TODO*///			case UI_GAMEINFO:
    /*TODO*///			case UI_IMAGEINFO:
    /*TODO*///			case UI_FILEMANAGER:
    /*TODO*///			case UI_TAPECONTROL:
    /*TODO*///			#endif
    /*TODO*///			case UI_HISTORY:
    /*TODO*///			case UI_CHEAT:
    /*TODO*///			case UI_MEMCARD:
    /*TODO*///				sel |= 1 << SEL_BITS;
    /*TODO*///				/* tell updatescreen() to clean after us */
    /*TODO*///				need_to_clear_bitmap = 1;
    /*TODO*///				break;
    /*TODO*///
    /*TODO*///			case UI_RESET:
    /*TODO*///				machine_reset();
    /*TODO*///				break;
    /*TODO*///
    /*TODO*///			case UI_EXIT:
    /*TODO*///				menu_lastselected = 0;
    /*TODO*///				sel = -1;
    /*TODO*///				break;
    /*TODO*///		}
    /*TODO*///	}
    /*TODO*///
    /*TODO*///	if (input_ui_pressed(IPT_UI_CANCEL) ||
    /*TODO*///			input_ui_pressed(IPT_UI_CONFIGURE))
    /*TODO*///	{
    /*TODO*///		menu_lastselected = sel;
    /*TODO*///		sel = -1;
    /*TODO*///	}
    /*TODO*///
    /*TODO*///	if (sel == -1)
    /*TODO*///	{
    /*TODO*///		/* tell updatescreen() to clean after us */
    /*TODO*///		need_to_clear_bitmap = 1;
    /*TODO*///	}
    /*TODO*///
    /*TODO*///	return sel + 1;
    /*TODO*///}
    /*TODO*///
    /*TODO*///
    /*TODO*///
    /*TODO*////*********************************************************************
    /*TODO*///
    /*TODO*///  start of On Screen Display handling
    /*TODO*///
    /*TODO*///*********************************************************************/
    /*TODO*///
    /*TODO*///static void displayosd(const char *text,int percentage,int default_percentage)
    /*TODO*///{
    /*TODO*///	struct DisplayText dt[2];
    /*TODO*///	int avail;
    /*TODO*///
    /*TODO*///
    /*TODO*///	avail = (Machine->uiwidth / Machine->uifontwidth) * 19 / 20;
    /*TODO*///
    /*TODO*///	ui_drawbox((Machine->uiwidth - Machine->uifontwidth * avail) / 2,
    /*TODO*///			(Machine->uiheight - 7*Machine->uifontheight/2),
    /*TODO*///			avail * Machine->uifontwidth,
    /*TODO*///			3*Machine->uifontheight);
    /*TODO*///
    /*TODO*///	avail--;
    /*TODO*///
    /*TODO*///	drawbar((Machine->uiwidth - Machine->uifontwidth * avail) / 2,
    /*TODO*///			(Machine->uiheight - 3*Machine->uifontheight),
    /*TODO*///			avail * Machine->uifontwidth,
    /*TODO*///			Machine->uifontheight,
    /*TODO*///			percentage,default_percentage);
    /*TODO*///
    /*TODO*///	dt[0].text = text;
    /*TODO*///	dt[0].color = DT_COLOR_WHITE;
    /*TODO*///	dt[0].x = (Machine->uiwidth - Machine->uifontwidth * strlen(text)) / 2;
    /*TODO*///	dt[0].y = (Machine->uiheight - 2*Machine->uifontheight) + 2;
    /*TODO*///	dt[1].text = 0; /* terminate array */
    /*TODO*///	displaytext(dt,0,0);
    /*TODO*///}
    /*TODO*///
    /*TODO*///
    /*TODO*///
    /*TODO*///static void onscrd_volume(int increment,int arg)
    /*TODO*///{
    /*TODO*///	char buf[20];
    /*TODO*///	int attenuation;
    /*TODO*///
    /*TODO*///	if (increment)
    /*TODO*///	{
    /*TODO*///		attenuation = osd_get_mastervolume();
    /*TODO*///		attenuation += increment;
    /*TODO*///		if (attenuation > 0) attenuation = 0;
    /*TODO*///		if (attenuation < -32) attenuation = -32;
    /*TODO*///		osd_set_mastervolume(attenuation);
    /*TODO*///	}
    /*TODO*///	attenuation = osd_get_mastervolume();
    /*TODO*///
    /*TODO*///	sprintf(buf,"Volume %3ddB",attenuation);
    /*TODO*///	displayosd(buf,100 * (attenuation + 32) / 32,100);
    /*TODO*///}
    /*TODO*///
    /*TODO*///static void onscrd_mixervol(int increment,int arg)
    /*TODO*///{
    /*TODO*///	static void *driver = 0;
    /*TODO*///	char buf[40];
    /*TODO*///	int volume,ch;
    /*TODO*///	int doallchannels = 0;
    /*TODO*///	int proportional = 0;
    /*TODO*///
    /*TODO*///
    /*TODO*///	if (code_pressed(KEYCODE_LSHIFT) || code_pressed(KEYCODE_RSHIFT))
    /*TODO*///		doallchannels = 1;
    /*TODO*///	if (!code_pressed(KEYCODE_LCONTROL) && !code_pressed(KEYCODE_RCONTROL))
    /*TODO*///		increment *= 5;
    /*TODO*///	if (code_pressed(KEYCODE_LALT) || code_pressed(KEYCODE_RALT))
    /*TODO*///		proportional = 1;
    /*TODO*///
    /*TODO*///	if (increment)
    /*TODO*///	{
    /*TODO*///		if (proportional)
    /*TODO*///		{
    /*TODO*///			static int old_vol[MIXER_MAX_CHANNELS];
    /*TODO*///			float ratio = 1.0;
    /*TODO*///			int overflow = 0;
    /*TODO*///
    /*TODO*///			if (driver != Machine->drv)
    /*TODO*///			{
    /*TODO*///				driver = (void *)Machine->drv;
    /*TODO*///				for (ch = 0; ch < MIXER_MAX_CHANNELS; ch++)
    /*TODO*///					old_vol[ch] = mixer_get_mixing_level(ch);
    /*TODO*///			}
    /*TODO*///
    /*TODO*///			volume = mixer_get_mixing_level(arg);
    /*TODO*///			if (old_vol[arg])
    /*TODO*///				ratio = (float)(volume + increment) / (float)old_vol[arg];
    /*TODO*///
    /*TODO*///			for (ch = 0; ch < MIXER_MAX_CHANNELS; ch++)
    /*TODO*///			{
    /*TODO*///				if (mixer_get_name(ch) != 0)
    /*TODO*///				{
    /*TODO*///					volume = ratio * old_vol[ch];
    /*TODO*///					if (volume < 0 || volume > 100)
    /*TODO*///						overflow = 1;
    /*TODO*///				}
    /*TODO*///			}
    /*TODO*///
    /*TODO*///			if (!overflow)
    /*TODO*///			{
    /*TODO*///				for (ch = 0; ch < MIXER_MAX_CHANNELS; ch++)
    /*TODO*///				{
    /*TODO*///					volume = ratio * old_vol[ch];
    /*TODO*///					mixer_set_mixing_level(ch,volume);
    /*TODO*///				}
    /*TODO*///			}
    /*TODO*///		}
    /*TODO*///		else
    /*TODO*///		{
    /*TODO*///			driver = 0; /* force reset of saved volumes */
    /*TODO*///
    /*TODO*///			volume = mixer_get_mixing_level(arg);
    /*TODO*///			volume += increment;
    /*TODO*///			if (volume > 100) volume = 100;
    /*TODO*///			if (volume < 0) volume = 0;
    /*TODO*///
    /*TODO*///			if (doallchannels)
    /*TODO*///			{
    /*TODO*///				for (ch = 0;ch < MIXER_MAX_CHANNELS;ch++)
    /*TODO*///					mixer_set_mixing_level(ch,volume);
    /*TODO*///			}
    /*TODO*///			else
    /*TODO*///				mixer_set_mixing_level(arg,volume);
    /*TODO*///		}
    /*TODO*///	}
    /*TODO*///	volume = mixer_get_mixing_level(arg);
    /*TODO*///
    /*TODO*///	if (proportional)
    /*TODO*///		sprintf(buf,"ALL CHANNELS Relative %3d%%", volume);
    /*TODO*///	else if (doallchannels)
    /*TODO*///		sprintf(buf,"ALL CHANNELS Volume %3d%%",volume);
    /*TODO*///	else
    /*TODO*///		sprintf(buf,"%s Volume %3d%%",mixer_get_name(arg),volume);
    /*TODO*///	displayosd(buf,volume,mixer_get_default_mixing_level(arg));
    /*TODO*///}
    /*TODO*///
    /*TODO*///static void onscrd_brightness(int increment,int arg)
    /*TODO*///{
    /*TODO*///	char buf[20];
    /*TODO*///	int brightness;
    /*TODO*///
    /*TODO*///
    /*TODO*///	if (increment)
    /*TODO*///	{
    /*TODO*///		brightness = osd_get_brightness();
    /*TODO*///		brightness += 5 * increment;
    /*TODO*///		if (brightness < 0) brightness = 0;
    /*TODO*///		if (brightness > 100) brightness = 100;
    /*TODO*///		osd_set_brightness(brightness);
    /*TODO*///	}
    /*TODO*///	brightness = osd_get_brightness();
    /*TODO*///
    /*TODO*///	sprintf(buf,"Brightness %3d%%",brightness);
    /*TODO*///	displayosd(buf,brightness,100);
    /*TODO*///}
    /*TODO*///
    /*TODO*///static void onscrd_gamma(int increment,int arg)
    /*TODO*///{
    /*TODO*///	char buf[20];
    /*TODO*///	float gamma_correction;
    /*TODO*///
    /*TODO*///	if (increment)
    /*TODO*///	{
    /*TODO*///		gamma_correction = osd_get_gamma();
    /*TODO*///
    /*TODO*///		gamma_correction += 0.05 * increment;
    /*TODO*///		if (gamma_correction < 0.5) gamma_correction = 0.5;
    /*TODO*///		if (gamma_correction > 2.0) gamma_correction = 2.0;
    /*TODO*///
    /*TODO*///		osd_set_gamma(gamma_correction);
    /*TODO*///	}
    /*TODO*///	gamma_correction = osd_get_gamma();
    /*TODO*///
    /*TODO*///	sprintf(buf,"Gamma %1.2f",gamma_correction);
    /*TODO*///	displayosd(buf,100*(gamma_correction-0.5)/(2.0-0.5),100*(1.0-0.5)/(2.0-0.5));
    /*TODO*///}
    /*TODO*///
    /*TODO*///static void onscrd_vector_intensity(int increment,int arg)
    /*TODO*///{
    /*TODO*///	char buf[30];
    /*TODO*///	float intensity_correction;
    /*TODO*///
    /*TODO*///	if (increment)
    /*TODO*///	{
    /*TODO*///		intensity_correction = vector_get_intensity();
    /*TODO*///
    /*TODO*///		intensity_correction += 0.05 * increment;
    /*TODO*///		if (intensity_correction < 0.5) intensity_correction = 0.5;
    /*TODO*///		if (intensity_correction > 3.0) intensity_correction = 3.0;
    /*TODO*///
    /*TODO*///		vector_set_intensity(intensity_correction);
    /*TODO*///	}
    /*TODO*///	intensity_correction = vector_get_intensity();
    /*TODO*///
    /*TODO*///	sprintf(buf,"Vector intensity %1.2f",intensity_correction);
    /*TODO*///	displayosd(buf,100*(intensity_correction-0.5)/(3.0-0.5),100*(1.5-0.5)/(3.0-0.5));
    /*TODO*///}
    /*TODO*///
    /*TODO*///
    /*TODO*///static void onscrd_overclock(int increment,int arg)
    /*TODO*///{
    /*TODO*///	char buf[30];
    /*TODO*///	double overclock;
    /*TODO*///	int cpu, doallcpus = 0, oc;
    /*TODO*///
    /*TODO*///	if (code_pressed(KEYCODE_LSHIFT) || code_pressed(KEYCODE_RSHIFT))
    /*TODO*///		doallcpus = 1;
    /*TODO*///	if (!code_pressed(KEYCODE_LCONTROL) && !code_pressed(KEYCODE_RCONTROL))
    /*TODO*///		increment *= 5;
    /*TODO*///	if( increment )
    /*TODO*///	{
    /*TODO*///		overclock = timer_get_overclock(arg);
    /*TODO*///		overclock += 0.01 * increment;
    /*TODO*///		if (overclock < 0.01) overclock = 0.01;
    /*TODO*///		if (overclock > 2.0) overclock = 2.0;
    /*TODO*///		if( doallcpus )
    /*TODO*///			for( cpu = 0; cpu < cpu_gettotalcpu(); cpu++ )
    /*TODO*///				timer_set_overclock(cpu, overclock);
    /*TODO*///		else
    /*TODO*///			timer_set_overclock(arg, overclock);
    /*TODO*///	}
    /*TODO*///
    /*TODO*///	oc = 100 * timer_get_overclock(arg) + 0.5;
    /*TODO*///
    /*TODO*///	if( doallcpus )
    /*TODO*///		sprintf(buf,"ALL CPUS Overclock %3d%%", oc);
    /*TODO*///	else
    /*TODO*///		sprintf(buf,"Overclock CPU#%d %3d%%", arg, oc);
    /*TODO*///	displayosd(buf,oc/2,100/2);
    /*TODO*///}
    /*TODO*///
    /*TODO*///#define MAX_OSD_ITEMS 30
    /*TODO*///static void (*onscrd_fnc[MAX_OSD_ITEMS])(int increment,int arg);
    /*TODO*///static int onscrd_arg[MAX_OSD_ITEMS];
    /*TODO*///static int onscrd_total_items;
    /*TODO*///
    /*TODO*///static void onscrd_init(void)
    /*TODO*///{
    /*TODO*///	int item,ch;
    /*TODO*///
    /*TODO*///
    /*TODO*///	item = 0;
    /*TODO*///
    /*TODO*///	onscrd_fnc[item] = onscrd_volume;
    /*TODO*///	onscrd_arg[item] = 0;
    /*TODO*///	item++;
    /*TODO*///
    /*TODO*///	for (ch = 0;ch < MIXER_MAX_CHANNELS;ch++)
    /*TODO*///	{
    /*TODO*///		if (mixer_get_name(ch) != 0)
    /*TODO*///		{
    /*TODO*///			onscrd_fnc[item] = onscrd_mixervol;
    /*TODO*///			onscrd_arg[item] = ch;
    /*TODO*///			item++;
    /*TODO*///		}
    /*TODO*///	}
    /*TODO*///
    /*TODO*///	if (options.cheat)
    /*TODO*///	{
    /*TODO*///		for (ch = 0;ch < cpu_gettotalcpu();ch++)
    /*TODO*///		{
    /*TODO*///			onscrd_fnc[item] = onscrd_overclock;
    /*TODO*///			onscrd_arg[item] = ch;
    /*TODO*///			item++;
    /*TODO*///		}
    /*TODO*///	}
    /*TODO*///
    /*TODO*///	onscrd_fnc[item] = onscrd_brightness;
    /*TODO*///	onscrd_arg[item] = 0;
    /*TODO*///	item++;
    /*TODO*///
    /*TODO*///	onscrd_fnc[item] = onscrd_gamma;
    /*TODO*///	onscrd_arg[item] = 0;
    /*TODO*///	item++;
    /*TODO*///
    /*TODO*///	if (Machine->drv->video_attributes & VIDEO_TYPE_VECTOR)
    /*TODO*///	{
    /*TODO*///		onscrd_fnc[item] = onscrd_vector_intensity;
    /*TODO*///		onscrd_arg[item] = 0;
    /*TODO*///		item++;
    /*TODO*///	}
    /*TODO*///
    /*TODO*///	onscrd_total_items = item;
    /*TODO*///}
    /*TODO*///
    /*TODO*///static int on_screen_display(int selected)
    /*TODO*///{
    /*TODO*///	int increment,sel;
    /*TODO*///	static int lastselected = 0;
    /*TODO*///
    /*TODO*///
    /*TODO*///	if (selected == -1)
    /*TODO*///		sel = lastselected;
    /*TODO*///	else sel = selected - 1;
    /*TODO*///
    /*TODO*///	increment = 0;
    /*TODO*///	if (input_ui_pressed_repeat(IPT_UI_LEFT,8))
    /*TODO*///		increment = -1;
    /*TODO*///	if (input_ui_pressed_repeat(IPT_UI_RIGHT,8))
    /*TODO*///		increment = 1;
    /*TODO*///	if (input_ui_pressed_repeat(IPT_UI_DOWN,8))
    /*TODO*///		sel = (sel + 1) % onscrd_total_items;
    /*TODO*///	if (input_ui_pressed_repeat(IPT_UI_UP,8))
    /*TODO*///		sel = (sel + onscrd_total_items - 1) % onscrd_total_items;
    /*TODO*///
    /*TODO*///	(*onscrd_fnc[sel])(increment,onscrd_arg[sel]);
    /*TODO*///
    /*TODO*///	lastselected = sel;
    /*TODO*///
    /*TODO*///	if (input_ui_pressed(IPT_UI_ON_SCREEN_DISPLAY))
    /*TODO*///	{
    /*TODO*///		sel = -1;
    /*TODO*///
    /*TODO*///		/* tell updatescreen() to clean after us */
    /*TODO*///		need_to_clear_bitmap = 1;
    /*TODO*///	}
    /*TODO*///
    /*TODO*///	return sel + 1;
    /*TODO*///}
    /*TODO*///
    /*TODO*////*********************************************************************
    /*TODO*///
    /*TODO*///  end of On Screen Display handling
    /*TODO*///
    /*TODO*///*********************************************************************/
    /*TODO*///
    /*TODO*///
    /*TODO*///static void displaymessage(const char *text)
    /*TODO*///{
    /*TODO*///	struct DisplayText dt[2];
    /*TODO*///	int avail;
    /*TODO*///
    /*TODO*///
    /*TODO*///	if (Machine->uiwidth < Machine->uifontwidth * strlen(text))
    /*TODO*///	{
    /*TODO*///		ui_displaymessagewindow(text);
    /*TODO*///		return;
    /*TODO*///	}
    /*TODO*///
    /*TODO*///	avail = strlen(text)+2;
    /*TODO*///
    /*TODO*///	ui_drawbox((Machine->uiwidth - Machine->uifontwidth * avail) / 2,
    /*TODO*///			Machine->uiheight - 3*Machine->uifontheight,
    /*TODO*///			avail * Machine->uifontwidth,
    /*TODO*///			2*Machine->uifontheight);
    /*TODO*///
    /*TODO*///	dt[0].text = text;
    /*TODO*///	dt[0].color = DT_COLOR_WHITE;
    /*TODO*///	dt[0].x = (Machine->uiwidth - Machine->uifontwidth * strlen(text)) / 2;
    /*TODO*///	dt[0].y = Machine->uiheight - 5*Machine->uifontheight/2;
    /*TODO*///	dt[1].text = 0; /* terminate array */
    /*TODO*///	displaytext(dt,0,0);
    /*TODO*///}
    /*TODO*///
    /*TODO*///
    /*TODO*///static char messagetext[80];
    /*TODO*///static int messagecounter;
    /*TODO*///
    /*TODO*///void CLIB_DECL usrintf_showmessage(const char *text,...)
    /*TODO*///{
    /*TODO*///	va_list arg;
    /*TODO*///	va_start(arg,text);
    /*TODO*///	vsprintf(messagetext,text,arg);
    /*TODO*///	va_end(arg);
    /*TODO*///	messagecounter = 2 * Machine->drv->frames_per_second;
    /*TODO*///}
    /*TODO*///
    /*TODO*///
    /*TODO*///
    /*TODO*///
    /*TODO*///int handle_user_interface(void)
    /*TODO*///{
    /*TODO*///	static int show_profiler;
    /*TODO*///#ifdef MAME_DEBUG
    /*TODO*///	static int show_total_colors;
    /*TODO*///#endif
    /*TODO*///
    /*TODO*///
    /*TODO*///	/* if the user pressed F12, save the screen to a file */
    /*TODO*///	if (input_ui_pressed(IPT_UI_SNAPSHOT))
    /*TODO*///		osd_save_snapshot();
    /*TODO*///
    /*TODO*///	/* This call is for the cheat, it must be called at least each frames */
    /*TODO*///	if (options.cheat) DoCheat();
    /*TODO*///
    /*TODO*///	/* if the user pressed ESC, stop the emulation */
    /*TODO*///	/* but don't quit if the setup menu is on screen */
    /*TODO*///	if (setup_selected == 0 && input_ui_pressed(IPT_UI_CANCEL))
    /*TODO*///		return 1;
    /*TODO*///
    /*TODO*///	if (setup_selected == 0 && input_ui_pressed(IPT_UI_CONFIGURE))
    /*TODO*///	{
    /*TODO*///		setup_selected = -1;
    /*TODO*///		if (osd_selected != 0)
    /*TODO*///		{
    /*TODO*///			osd_selected = 0;	/* disable on screen display */
    /*TODO*///			/* tell updatescreen() to clean after us */
    /*TODO*///			need_to_clear_bitmap = 1;
    /*TODO*///		}
    /*TODO*///	}
    /*TODO*///	if (setup_selected != 0) setup_selected = setup_menu(setup_selected);
    /*TODO*///
    /*TODO*///	if (!mame_debug && osd_selected == 0 && input_ui_pressed(IPT_UI_ON_SCREEN_DISPLAY))
    /*TODO*///	{
    /*TODO*///		osd_selected = -1;
    /*TODO*///		if (setup_selected != 0)
    /*TODO*///		{
    /*TODO*///			setup_selected = 0; /* disable setup menu */
    /*TODO*///			/* tell updatescreen() to clean after us */
    /*TODO*///			need_to_clear_bitmap = 1;
    /*TODO*///		}
    /*TODO*///	}
    /*TODO*///	if (osd_selected != 0) osd_selected = on_screen_display(osd_selected);
    /*TODO*///
    /*TODO*///
    /*TODO*///#if 0
    /*TODO*///	if (keyboard_pressed_memory(KEYCODE_BACKSPACE))
    /*TODO*///	{
    /*TODO*///		if (jukebox_selected != -1)
    /*TODO*///		{
    /*TODO*///			jukebox_selected = -1;
    /*TODO*///			cpu_halt(0,1);
    /*TODO*///		}
    /*TODO*///		else
    /*TODO*///		{
    /*TODO*///			jukebox_selected = 0;
    /*TODO*///			cpu_halt(0,0);
    /*TODO*///		}
    /*TODO*///	}
    /*TODO*///
    /*TODO*///	if (jukebox_selected != -1)
    /*TODO*///	{
    /*TODO*///		char buf[40];
    /*TODO*///		watchdog_reset_w(0,0);
    /*TODO*///		if (keyboard_pressed_memory(KEYCODE_LCONTROL))
    /*TODO*///		{
    /*TODO*///#include "cpu/z80/z80.h"
    /*TODO*///			soundlatch_w(0,jukebox_selected);
    /*TODO*///			cpu_cause_interrupt(1,Z80_NMI_INT);
    /*TODO*///		}
    /*TODO*///		if (input_ui_pressed_repeat(IPT_UI_RIGHT,8))
    /*TODO*///		{
    /*TODO*///			jukebox_selected = (jukebox_selected + 1) & 0xff;
    /*TODO*///		}
    /*TODO*///		if (input_ui_pressed_repeat(IPT_UI_LEFT,8))
    /*TODO*///		{
    /*TODO*///			jukebox_selected = (jukebox_selected - 1) & 0xff;
    /*TODO*///		}
    /*TODO*///		if (input_ui_pressed_repeat(IPT_UI_UP,8))
    /*TODO*///		{
    /*TODO*///			jukebox_selected = (jukebox_selected + 16) & 0xff;
    /*TODO*///		}
    /*TODO*///		if (input_ui_pressed_repeat(IPT_UI_DOWN,8))
    /*TODO*///		{
    /*TODO*///			jukebox_selected = (jukebox_selected - 16) & 0xff;
    /*TODO*///		}
    /*TODO*///		sprintf(buf,"sound cmd %02x",jukebox_selected);
    /*TODO*///		displaymessage(buf);
    /*TODO*///	}
    /*TODO*///#endif
    /*TODO*///
    /*TODO*///
    /*TODO*///	/* if the user pressed F3, reset the emulation */
    /*TODO*///	if (input_ui_pressed(IPT_UI_RESET_MACHINE))
    /*TODO*///		machine_reset();
    /*TODO*///
    /*TODO*///
    /*TODO*///	if (single_step || input_ui_pressed(IPT_UI_PAUSE)) /* pause the game */
    /*TODO*///	{
    /*TODO*////*		osd_selected = 0;	   disable on screen display, since we are going   */
    /*TODO*///							/* to change parameters affected by it */
    /*TODO*///
    /*TODO*///		if (single_step == 0)
    /*TODO*///		{
    /*TODO*///			osd_sound_enable(0);
    /*TODO*///			osd_pause(1);
    /*TODO*///		}
    /*TODO*///
    /*TODO*///		while (!input_ui_pressed(IPT_UI_PAUSE))
    /*TODO*///		{
    /*TODO*///#ifdef MAME_NET
    /*TODO*///			osd_net_sync();
    /*TODO*///#endif /* MAME_NET */
    /*TODO*///			profiler_mark(PROFILER_VIDEO);
    /*TODO*///			if (osd_skip_this_frame() == 0)
    /*TODO*///			{
    /*TODO*///				if (need_to_clear_bitmap || bitmap_dirty)
    /*TODO*///				{
    /*TODO*///					osd_clearbitmap(Machine->scrbitmap);
    /*TODO*///					need_to_clear_bitmap = 0;
    /*TODO*///					(*Machine->drv->vh_update)(Machine->scrbitmap,bitmap_dirty);
    /*TODO*///					bitmap_dirty = 0;
    /*TODO*///				}
    /*TODO*///#ifdef MAME_DEBUG
    /*TODO*////* keep calling vh_screenrefresh() while paused so we can stuff */
    /*TODO*////* debug code in there */
    /*TODO*///(*Machine->drv->vh_update)(Machine->scrbitmap,bitmap_dirty);
    /*TODO*///#endif
    /*TODO*///			}
    /*TODO*///			profiler_mark(PROFILER_END);
    /*TODO*///
    /*TODO*///			if (input_ui_pressed(IPT_UI_SNAPSHOT))
    /*TODO*///				osd_save_snapshot();
    /*TODO*///
    /*TODO*///			if (setup_selected == 0 && input_ui_pressed(IPT_UI_CANCEL))
    /*TODO*///				return 1;
    /*TODO*///
    /*TODO*///			if (setup_selected == 0 && input_ui_pressed(IPT_UI_CONFIGURE))
    /*TODO*///			{
    /*TODO*///				setup_selected = -1;
    /*TODO*///				if (osd_selected != 0)
    /*TODO*///				{
    /*TODO*///					osd_selected = 0;	/* disable on screen display */
    /*TODO*///					/* tell updatescreen() to clean after us */
    /*TODO*///					need_to_clear_bitmap = 1;
    /*TODO*///				}
    /*TODO*///			}
    /*TODO*///			if (setup_selected != 0) setup_selected = setup_menu(setup_selected);
    /*TODO*///
    /*TODO*///			if (!mame_debug && osd_selected == 0 && input_ui_pressed(IPT_UI_ON_SCREEN_DISPLAY))
    /*TODO*///			{
    /*TODO*///				osd_selected = -1;
    /*TODO*///				if (setup_selected != 0)
    /*TODO*///				{
    /*TODO*///					setup_selected = 0; /* disable setup menu */
    /*TODO*///					/* tell updatescreen() to clean after us */
    /*TODO*///					need_to_clear_bitmap = 1;
    /*TODO*///				}
    /*TODO*///			}
    /*TODO*///			if (osd_selected != 0) osd_selected = on_screen_display(osd_selected);
    /*TODO*///
    /*TODO*///			/* show popup message if any */
    /*TODO*///			if (messagecounter > 0) displaymessage(messagetext);
    /*TODO*///
    /*TODO*///			osd_update_video_and_audio();
    /*TODO*///			osd_poll_joysticks();
    /*TODO*///		}
    /*TODO*///
    /*TODO*///		if (code_pressed(KEYCODE_LSHIFT) || code_pressed(KEYCODE_RSHIFT))
    /*TODO*///			single_step = 1;
    /*TODO*///		else
    /*TODO*///		{
    /*TODO*///			single_step = 0;
    /*TODO*///			osd_pause(0);
    /*TODO*///			osd_sound_enable(1);
    /*TODO*///		}
    /*TODO*///	}
    /*TODO*///
    /*TODO*///
    /*TODO*///	/* show popup message if any */
    /*TODO*///	if (messagecounter > 0)
    /*TODO*///	{
    /*TODO*///		displaymessage(messagetext);
    /*TODO*///
    /*TODO*///		if (--messagecounter == 0)
    /*TODO*///			/* tell updatescreen() to clean after us */
    /*TODO*///			need_to_clear_bitmap = 1;
    /*TODO*///	}
    /*TODO*///
    /*TODO*///
    /*TODO*///	if (input_ui_pressed(IPT_UI_SHOW_PROFILER))
    /*TODO*///	{
    /*TODO*///		show_profiler ^= 1;
    /*TODO*///		if (show_profiler)
    /*TODO*///			profiler_start();
    /*TODO*///		else
    /*TODO*///		{
    /*TODO*///			profiler_stop();
    /*TODO*///			/* tell updatescreen() to clean after us */
    /*TODO*///			need_to_clear_bitmap = 1;
    /*TODO*///		}
    /*TODO*///	}
    /*TODO*///#ifdef MAME_DEBUG
    /*TODO*///	if (input_ui_pressed(IPT_UI_SHOW_COLORS))
    /*TODO*///	{
    /*TODO*///		show_total_colors ^= 1;
    /*TODO*///		if (show_total_colors == 0)
    /*TODO*///			/* tell updatescreen() to clean after us */
    /*TODO*///			need_to_clear_bitmap = 1;
    /*TODO*///	}
    /*TODO*///	if (show_total_colors) showtotalcolors();
    /*TODO*///#endif
    /*TODO*///
    /*TODO*///	if (show_profiler) profiler_show();
    /*TODO*///
    /*TODO*///
    /*TODO*///	/* if the user pressed F4, show the character set */
    /*TODO*///	if (input_ui_pressed(IPT_UI_SHOW_GFX))
    /*TODO*///	{
    /*TODO*///		osd_sound_enable(0);
    /*TODO*///
    /*TODO*///		showcharset();
    /*TODO*///
    /*TODO*///		osd_sound_enable(1);
    /*TODO*///	}
    /*TODO*///
    /*TODO*///	return 0;
    /*TODO*///}
    /*TODO*///
    /*TODO*///
    /*TODO*///void init_user_interface(void)
    /*TODO*///{
    /*TODO*///	extern int snapno;	/* in common.c */
    /*TODO*///
    /*TODO*///	snapno = 0; /* reset snapshot counter */
    /*TODO*///
    /*TODO*///	setup_menu_init();
    /*TODO*///	setup_selected = 0;
    /*TODO*///
    /*TODO*///	onscrd_init();
    /*TODO*///	osd_selected = 0;
    /*TODO*///
    /*TODO*///	jukebox_selected = -1;
    /*TODO*///
    /*TODO*///	single_step = 0;
    /*TODO*///}
    /*TODO*///
    /*TODO*///int onscrd_active(void)
    /*TODO*///{
    /*TODO*///	return osd_selected;
    /*TODO*///}
    /*TODO*///
    /*TODO*///int setup_active(void)
    /*TODO*///{
    /*TODO*///	return setup_selected;
    /*TODO*///}
    /*TODO*///
    /*TODO*///    
}
