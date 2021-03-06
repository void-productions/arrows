package misc.compress;

import java.util.LinkedList;
import java.nio.ByteBuffer;

import misc.Debug;
import player.property.Team;

public class Compressor
{
	public static final byte
		AVATAR_USER_PACKET_CID = 1,
		CLIENT_GAME_PLAYER_FRAME_UPDATE_CID = 2,
		DISCONNECT_USER_PACKET_CID = 3,
		GAME_FRAME_UPDATE_PACKET_CID = 4,
		GAME_POSITION_CID = 5,
		GAME_SIZE_CID = 6,
		GAME_VECTOR_CID = 7,
		IMAGE_ID_CID = 8,
		ITEM_USER_PACKET_CID = 9,
		LOCAL_CLIENT_GAME_PLAYER_FRAME_UPDATE_CID = 10,
		LOCK_USER_PACKET_CID = 11,
		LOBBY_PLAYER_CID = 12,
		LOBBY_PLAYERS_PACKET_CID = 13,
		LOGIN_USER_PACKET_CID = 14,
		MAP_PACKET_CID = 15,
		MINIMIZED_BULLET_CID = 16,
		MINIMIZED_EFFECT_CID = 17,
		MINIMIZED_GAME_PLAYER_CID = 18,
		MINIMIZED_SPINNABLE_ENTITY_CID = 19,
		PLAYER_CONTROLS_UPDATE_PACKET_CID = 20,
		PLAYER_STATS_CID = 21,
		SKILL_USER_PACKET_CID = 22,
		TEAM_CID = 23,
		TEAM_USER_PACKET_CID = 24,
		USER_PACKET_WITH_ID_CID = 25;

	private Compressor() {}

	public static byte[] compress(Compressable c)
	{	
		byte[] b;

		try
		{
			b = c.compress();
		} catch (Exception e)
		{
			Debug.warn("Compressor.compress(): Can't compress " + c);
			return new byte[]{};
		}

		byte[] bytes = new byte[b.length+1];
		for (int i = 0; i < b.length; i++)
		{
			bytes[i+1] = b[i];
		}
		bytes[0] = c.getCID();
		return bytes;
	}

	public static Compressable decompress(byte[] bytes)
	{
		CompressBuffer buffer = new CompressBuffer(cutCID(bytes));
		return buffer.cutByCID(bytes[0]);
	}

	private static byte[] cutCID(byte[] arg)
	{
		byte[] bytes = new byte[arg.length-1];
		for (int i = 0; i < bytes.length; i++)
		{
			bytes[i] = arg[i+1];
		}
		return bytes;
	}

	public static byte[] concat(byte[][] bytes)
	{
		int c = 0;
		int length = 0;
		for (int i = 0; i < bytes.length; i++)
			length += bytes[i].length;

		byte[] result = new byte[length];

		for (int i = 0; i < bytes.length; i++)
			for (int j = 0; j < bytes[i].length; j++)
			{
				result[c] = bytes[i][j];
				c++;
			}
		return result;
	}

	public static byte[] compressFloat(float f)
	{
		return ByteBuffer.allocate(4).putFloat(f).array();
	}

	public static byte[] compressInt(int value)
	{
		return new byte[] {(byte) (value >>> 24), (byte) (value >>> 16), (byte) (value >>> 8), (byte) value };
	}

	public static byte[] compressShort(short value)
	{
		return new byte[] {(byte) (value >>> 8), (byte) value };
	}

	public static byte[] compressShortArray(short[] shorts)
	{
		byte[] bytes = new byte[shorts.length*2 + 4];
		byte[] length = compressInt(shorts.length);

		for (int i = 0; i < 4; i++)
		{
			bytes[i] = length[i];
		}

		for (int j = 0; j < shorts.length; j++)
		{
			byte[] s = compressShort(shorts[j]);
			bytes[j*2+4] = s[0];
			bytes[j*2+5] = s[1];
		}

		return bytes;
	}

	public static byte[] compressBoolean(boolean b)
	{
		return b? new byte[]{1} : new byte[]{0};	
	}

	public static byte[] compressString(String s)
	{
		byte[] bytes = s.getBytes();
		return concat(new byte[][]{compressInt(bytes.length), bytes});
	}

	public static byte[] compressFloatArray(float[] f)
	{
		byte[] bytes = new byte[f.length*4 + 4];
		byte[] length = compressInt(f.length);

		for (int i = 0; i < 4; i++)
		{
			bytes[i] = length[i];
		}

		for (int j = 0; j < f.length; j++)
		{
			byte[] s = compressFloat(f[j]);
			bytes[j*4+4] = s[0];
			bytes[j*4+5] = s[1];
			bytes[j*4+6] = s[2];
			bytes[j*4+7] = s[3];
		}

		return bytes;
	}

	public static byte[] compressBooleanArray(boolean[] b)
	{
		byte[] bytes = new byte[b.length + 4];
		byte[] length = compressInt(b.length);

		for (int i = 0; i < 4; i++)
		{
			bytes[i] = length[i];
		}

		for (int j = 0; j < b.length; j++)
		{
			bytes[j+4] = compressBoolean(b[j])[0];
		}

		return bytes;
	}

	public static byte[] compressByteArray(byte[] b)
	{
		byte[] bytes = new byte[b.length + 4];
		byte[] length = compressInt(b.length);

		for (int i = 0; i < 4; i++)
		{
			bytes[i] = length[i];
		}

		for (int j = 0; j < b.length; j++)
		{
			bytes[j+4] = b[j];
		}

		return bytes;
		
	}

	public static byte[] compressList(LinkedList list)
	{
		byte[] length = compressInt(list.size());

		byte[][] bytes = new byte[list.size()+1][];

		bytes[0] = length;

		for (int i = 0; i < list.size(); i++)
		{
			bytes[i+1] = ((Compressable) list.get(i)).compress();
		}
		return concat(bytes);
	}

	public static byte[] compressIntIntArray(int[][] ints) // only works if length is constant
	{
		byte[] width = compressInt(ints.length);
		byte[] height = compressInt(ints[0].length);

		byte[] bytes = new byte[8];

		for (int i = 0; i < 4; i++)
		{
			bytes[i] = width[i];
			bytes[i+4] = height[i];
		}

		for (int x = 0; x < ints.length; x++)
			for (int y = 0; y < ints[0].length; y++)
			{
				byte[] tmp = compressInt(ints[x][y]);
				bytes = concat(new byte[][]{bytes, tmp});
			}
		return bytes;
	}
}
