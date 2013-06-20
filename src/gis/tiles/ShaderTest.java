package gis.tiles;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.lwjgl.opengl.ARBFragmentShader;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

public class ShaderTest {

  private boolean done = false; // game runs until done is set to true

  public ShaderTest() {
    init();

    while(!done) {
      if(Display.isCloseRequested()) {
        done = true;
      }
      render();
      Display.update();
    }

    Display.destroy();
  }

  private Box box;

  private void render() {
    GL11.glClear(GL11.GL_COLOR_BUFFER_BIT |
        GL11.GL_DEPTH_BUFFER_BIT);
    GL11.glLoadIdentity();
    box.draw();
  }

  private void init() {
    final int w = 1024;
    final int h = 768;

    try {
      Display.setDisplayMode(new DisplayMode(w, h));
      Display.setVSyncEnabled(true);
      Display.setTitle("Shader Setup");
      Display.create();
    } catch(final Exception e) {
      System.out.println("Error setting up display");
      System.exit(0);
    }

    GL11.glViewport(0, 0, w, h);
    GL11.glMatrixMode(GL11.GL_PROJECTION);
    GL11.glLoadIdentity();
    GLU.gluPerspective(45.0f, ((float) w / (float) h), 0.1f, 100.0f);
    GL11.glMatrixMode(GL11.GL_MODELVIEW);
    GL11.glLoadIdentity();
    GL11.glShadeModel(GL11.GL_SMOOTH);
    GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    GL11.glClearDepth(1.0f);
    GL11.glEnable(GL11.GL_DEPTH_TEST);
    GL11.glDepthFunc(GL11.GL_LEQUAL);
    GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT,
        GL11.GL_NICEST);
    box = new Box();
  }

  public static void main(final String[] args) {
    new ShaderTest();
  }

}

class Box {

  /*
   * if the shaders are setup ok we can use shaders, otherwise we just use
   * default settings
   */
  private boolean useShader;

  /*
   * program shader, to which is attached a vertex and fragment shaders. They
   * are set to 0 as a check because GL will assign unique int values to each
   */
  private int program = 0;

  public Box() {
    int vertShader = 0, fragShader = 0;

    try {
      vertShader = createShader("shaders/screen.vert",
          ARBVertexShader.GL_VERTEX_SHADER_ARB);
      fragShader = createShader("shaders/screen.frag",
          ARBFragmentShader.GL_FRAGMENT_SHADER_ARB);
    } catch(final Exception exc) {
      exc.printStackTrace();
      return;
    } finally {
      if(vertShader == 0 || fragShader == 0) return;
    }

    program = ARBShaderObjects.glCreateProgramObjectARB();

    if(program == 0) return;

    /*
     * if the vertex and fragment shaders setup sucessfully, attach them to the
     * shader program, link the sahder program (into the GL context I suppose),
     * and validate
     */
    ARBShaderObjects.glAttachObjectARB(program, vertShader);
    ARBShaderObjects.glAttachObjectARB(program, fragShader);

    ARBShaderObjects.glLinkProgramARB(program);
    if(ARBShaderObjects.glGetObjectParameteriARB(program,
        ARBShaderObjects.GL_OBJECT_LINK_STATUS_ARB) == GL11.GL_FALSE) {
      System.err.println(getLogInfo(program));
      return;
    }

    ARBShaderObjects.glValidateProgramARB(program);
    if(ARBShaderObjects.glGetObjectParameteriARB(program,
        ARBShaderObjects.GL_OBJECT_VALIDATE_STATUS_ARB) == GL11.GL_FALSE) {
      System.err.println(getLogInfo(program));
      return;
    }

    useShader = true;
  }

  /*
   * If the shader was setup succesfully, we use the shader. Otherwise we run
   * normal drawing code.
   */
  public void draw() {
    if(useShader) {
      ARBShaderObjects.glUseProgramObjectARB(program);
    }

    GL11.glLoadIdentity();
    GL11.glTranslatef(0.0f, 0.0f, -10.0f);
    GL11.glColor3f(1.0f, 1.0f, 1.0f);// white

    GL11.glBegin(GL11.GL_QUADS);
    GL11.glVertex3f(-1.0f, 1.0f, 0.0f);
    GL11.glVertex3f(1.0f, 1.0f, 0.0f);
    GL11.glVertex3f(1.0f, -1.0f, 0.0f);
    GL11.glVertex3f(-1.0f, -1.0f, 0.0f);
    GL11.glEnd();

    // release the shader
    if(useShader) {
      ARBShaderObjects.glUseProgramObjectARB(0);
    }

  }

  /*
   * With the exception of syntax, setting up vertex and fragment shaders is the
   * same.
   * @param the name and path to the vertex shader
   */
  private static int createShader(final String filename, final int shaderType)
      throws Exception {
    int shader = 0;
    try {
      shader = ARBShaderObjects.glCreateShaderObjectARB(shaderType);

      if(shader == 0) return 0;

      ARBShaderObjects.glShaderSourceARB(shader, readFileAsString(filename));
      ARBShaderObjects.glCompileShaderARB(shader);

      if(ARBShaderObjects.glGetObjectParameteriARB(shader,
          ARBShaderObjects.GL_OBJECT_COMPILE_STATUS_ARB) == GL11.GL_FALSE) throw new RuntimeException(
          "Error creating shader: " + getLogInfo(shader));

      return shader;
    } catch(final Exception exc) {
      ARBShaderObjects.glDeleteObjectARB(shader);
      throw exc;
    }
  }

  private static String getLogInfo(final int obj) {
    return ARBShaderObjects.glGetInfoLogARB(obj,
        ARBShaderObjects.glGetObjectParameteriARB(obj,
            ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB));
  }

  private static String readFileAsString(final String filename) throws Exception {
    final StringBuilder source = new StringBuilder();

    final FileInputStream in = new FileInputStream(filename);

    Exception exception = null;

    BufferedReader reader;
    try {
      reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));

      Exception innerExc = null;
      try {
        String line;
        while((line = reader.readLine()) != null) {
          source.append(line).append('\n');
        }
      } catch(final Exception exc) {
        exception = exc;
      } finally {
        try {
          reader.close();
        } catch(final Exception exc) {
          innerExc = exc;
        }
      }

      if(innerExc != null) throw innerExc;
    } catch(final Exception exc) {
      exception = exc;
    } finally {
      try {
        in.close();
      } catch(final Exception exc) {
        if(exception == null) {
          exception = exc;
        } else {
          exc.printStackTrace();
        }
      }

      if(exception != null) throw exception;
    }

    return source.toString();
  }
}
