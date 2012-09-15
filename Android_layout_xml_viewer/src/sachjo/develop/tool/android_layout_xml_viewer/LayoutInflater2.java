/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// Downloaded from http://www.netmite.com/android/mydroid/frameworks/base/core/java/android/view/LayoutInflater.java
// and modified by sachjo@gmail.com

package sachjo.develop.tool.android_layout_xml_viewer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.XmlResourceParser;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Xml;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * This class is used to instantiate layout XML file into its corresponding View
 * objects. It is never be used directly -- use
 * {@link android.app.Activity#getLayoutInflater()} or
 * {@link Context#getSystemService} to retrieve a standard LayoutInflater instance
 * that is already hooked up to the current context and correctly configured
 * for the device you are running on.  For example:
 *
 * <pre>LayoutInflater inflater = (LayoutInflater)context.getSystemService
 *      Context.LAYOUT_INFLATER_SERVICE);</pre>
 *
 * <p>
 * To create a new LayoutInflater with an additional {@link Factory} for your
 * own views, you can use {@link #cloneInContext} to clone an existing
 * ViewFactory, and then call {@link #setFactory} on it to include your
 * Factory.
 *
 * <p>
 * For performance reasons, view inflation relies heavily on pre-processing of
 * XML files that is done at build time. Therefore, it is not currently possible
 * to use LayoutInflater with an XmlPullParser over a plain XML file at runtime;
 * it only works with an XmlPullParser returned from a compiled resource
 * (R.<em>something</em> file.)
 *
 * @see Context#getSystemService
 */
public  class LayoutInflater2 extends LayoutInflater {
    protected LayoutInflater2(Context context) {
    	super(context);
		mContext = context;
		if (DEBUG) {
            System.out.println("constructor 1 called.");
        }
	}

    protected LayoutInflater2(LayoutInflater original, Context newContext,DisplayMetrics metrics) {
    	super(original,newContext);
		if (DEBUG) {
            System.out.println("constructor 2 called.");
        }
		mContext = newContext;
		mMetrics = metrics;
    }

	private final boolean DEBUG = true;

    /**
     * This field should be made private, so it is hidden from the SDK.
     * {@hide}
     */
    protected final Context mContext;

    private DisplayMetrics mMetrics;

    private static final String TAG_MERGE = "merge";
    private static final String TAG_INCLUDE = "include";
    private static final String TAG_REQUEST_FOCUS = "requestFocus";

    /**
     * Obtains the LayoutInflater from the given context.
     */
    public static LayoutInflater2 from(Context context) {
        LayoutInflater2 LayoutInflater =
                (LayoutInflater2) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (LayoutInflater == null) {
            throw new AssertionError("LayoutInflater not found.");
        }
        return LayoutInflater;
    }

    /**
     * Create a copy of the existing LayoutInflater object, with the copy
     * pointing to a different Context than the original.  This is used by
     * {@link ContextThemeWrapper} to create a new LayoutInflater to go along
     * with the new Context theme.
     *
     * @param newContext The new Context to associate with the new LayoutInflater.
     * May be the same as the original Context if desired.
     *
     * @return Returns a brand spanking new LayoutInflater object associated with
     * the given Context.
     */
    public  LayoutInflater2 cloneInContext(Context newContext) {
		return this;
    }

    /**
     * Return the context we are running in, for access to resources, class
     * loader, etc.
     */
    public Context getContext() {
        return mContext;
    }

    /**
     * Inflate a new view hierarchy from the specified xml resource. Throws
     * {@link InflateException} if there is an error.
     *
     * @param resource ID for an XML layout resource to load (e.g.,
     *        <code>R.layout.main_page</code>)
     * @param root Optional view to be the parent of the generated hierarchy.
     * @return The root View of the inflated hierarchy. If root was supplied,
     *         this is the root View; otherwise it is the root of the inflated
     *         XML file.
     */
    public View inflate(int resource, ViewGroup root) {
        return inflate(resource, root, root != null);
    }

    /**
     * Inflate a new view hierarchy from the specified xml node. Throws
     * {@link InflateException} if there is an error. *
     * <p>
     * <em><strong>Important</strong></em>&nbsp;&nbsp;&nbsp;For performance
     * reasons, view inflation relies heavily on pre-processing of XML files
     * that is done at build time. Therefore, it is not currently possible to
     * use LayoutInflater with an XmlPullParser over a plain XML file at runtime.
     *
     * @param parser XML dom node containing the description of the view
     *        hierarchy.
     * @param root Optional view to be the parent of the generated hierarchy.
     * @return The root View of the inflated hierarchy. If root was supplied,
     *         this is the root View; otherwise it is the root of the inflated
     *         XML file.
     */
    public View inflate(XmlPullParser parser, ViewGroup root) {
        return inflate(parser, root, root != null);
    }

    /**
     * Inflate a new view hierarchy from the specified xml resource. Throws
     * {@link InflateException} if there is an error.
     *
     * @param resource ID for an XML layout resource to load (e.g.,
     *        <code>R.layout.main_page</code>)
     * @param root Optional view to be the parent of the generated hierarchy (if
     *        <em>attachToRoot</em> is true), or else simply an object that
     *        provides a set of LayoutParams values for root of the returned
     *        hierarchy (if <em>attachToRoot</em> is false.)
     * @param attachToRoot Whether the inflated hierarchy should be attached to
     *        the root parameter? If false, root is only used to create the
     *        correct subclass of LayoutParams for the root view in the XML.
     * @return The root View of the inflated hierarchy. If root was supplied and
     *         attachToRoot is true, this is root; otherwise it is the root of
     *         the inflated XML file.
     */
    public View inflate(int resource, ViewGroup root, boolean attachToRoot) {
        if (DEBUG) System.out.println("INFLATING from resource: " + resource);
        XmlResourceParser parser = getContext().getResources().getLayout(resource);
        try {
            return inflate(parser, root, attachToRoot);
        } finally {
            parser.close();
        }
    }

    /**
     * Inflate a new view hierarchy from the specified XML node. Throws
     * {@link InflateException} if there is an error.
     * <p>
     * <em><strong>Important</strong></em>&nbsp;&nbsp;&nbsp;For performance
     * reasons, view inflation relies heavily on pre-processing of XML files
     * that is done at build time. Therefore, it is not currently possible to
     * use LayoutInflater with an XmlPullParser over a plain XML file at runtime.
     *
     * @param parser XML dom node containing the description of the view
     *        hierarchy.
     * @param root Optional view to be the parent of the generated hierarchy (if
     *        <em>attachToRoot</em> is true), or else simply an object that
     *        provides a set of LayoutParams values for root of the returned
     *        hierarchy (if <em>attachToRoot</em> is false.)
     * @param attachToRoot Whether the inflated hierarchy should be attached to
     *        the root parameter? If false, root is only used to create the
     *        correct subclass of LayoutParams for the root view in the XML.
     * @return The root View of the inflated hierarchy. If root was supplied and
     *         attachToRoot is true, this is root; otherwise it is the root of
     *         the inflated XML file.
     */
    public View inflate(XmlPullParser parser, ViewGroup root, boolean attachToRoot) {
        //synchronized (mConstructorArgs) {
            final AttributeSet attrs = Xml.asAttributeSet(parser);
            if (DEBUG) if(mContext == null) System.out.println("mContext is null");
            View result = root;

            try {
                // Look for the root node.
                int type;
                while ((type = parser.next()) != XmlPullParser.START_TAG &&
                        type != XmlPullParser.END_DOCUMENT) {
                    // Empty
                }

                if (type != XmlPullParser.START_TAG) {
                    throw new InflateException(parser.getPositionDescription()
                            + ": No start tag found!");
                }

                final String name = parser.getName();

                if (DEBUG) {
                    System.out.println("**************************");
                    System.out.println("Creating root view: "
                            + name);
                    System.out.println("**************************");
                }

                if (TAG_MERGE.equals(name)) {
                    if (root == null || !attachToRoot) {
                        throw new InflateException("<merge /> can be used only with a valid "
                                + "ViewGroup root and attachToRoot=true");
                    }

                    rInflate(parser, root, attrs);
                } else {
                    // Temp is the root view that was found in the xml
                    View temp = createViewFromTag(name, attrs);

                    ViewGroup.LayoutParams params = null;

                    if (root != null) {
                        if (DEBUG) {
                            System.out.println("Creating params from root: " +
                                    root);
                        }
                        // Create layout params that match root, if supplied
                        params = root.generateLayoutParams(attrs);
                        if (!attachToRoot) {
                            // Set the layout params for temp if we are not
                            // attaching. (If we are, we use addView, below)
                            temp.setLayoutParams(params);
                        }
                    }

                    if (DEBUG) {
                        System.out.println("-----> start inflating children");
                    }
                    // Inflate all children under temp
                    rInflate(parser, temp, attrs);
                    if (DEBUG) {
                        System.out.println("-----> done inflating children");
                    }

                    // We are supposed to attach all the views we found (int temp)
                    // to root. Do that now.
                    if (root != null && attachToRoot) {
                        root.addView(temp, params);
                    }

                    // Decide whether to return the root that was passed in or the
                    // top view found in xml.
                    if (root == null || !attachToRoot) {
                        result = temp;
                    }
                }

            } catch (XmlPullParserException e) {
                InflateException ex = new InflateException(e.getMessage());
                ex.initCause(e);
                throw ex;
            } catch (IOException e) {
                InflateException ex = new InflateException(
                        parser.getPositionDescription()
                        + ": " + e.getMessage());
                ex.initCause(e);
                throw ex;
            }

            return result;
        //}
    }

    /**
     * Low-level function for instantiating a view by name. This attempts to
     * instantiate a view class of the given <var>name</var> found in this
     * LayoutInflater's ClassLoader.
     *
     * <p>
     * There are two things that can happen in an error case: either the
     * exception describing the error will be thrown, or a null will be
     * returned. You must deal with both possibilities -- the former will happen
     * the first time createView() is called for a class of a particular name,
     * the latter every time there-after for that class name.
     *
     * @param name The full name of the class to be instantiated.
     * @param attrs The XML attributes supplied for this instance.
     *
     * @return View The newly instantied view, or null.
     */
    public View createView2(String name, String prefix, AttributeSet attrs)
            throws ClassNotFoundException, InflateException {
        try {
        	View retView;
        	if(name.equals("android.widget.RelativeLayout")) {
        		if (DEBUG) System.out.println("hello.");
        		//retView = (View) new RelativeLayout(mContext,attrs);
        		retView = (View) new RelativeLayout(mContext);
        		if (DEBUG) System.out.println("ok.");
        	}
        	else if(name.equals("android.widget.Button")) {
        		Button button = new Button(mContext);
        		String textStr = attrs.getAttributeValue(null,"text");
        		if(textStr != null) {
        			if(textStr.matches("@.+")){
        				textStr = mContext.getResources().getString(Integer.parseInt(textStr.substring(1)));
        			}
        			button.setText(textStr);
        		}
    			retView = (View) button;
        	}
        	else if(name.equals("android.widget.ListView")) {
        		retView = (View) new ListView(mContext);
        	}
        	else if(name.equals("android.widget.EditText")) {
        		EditText editText = new EditText(mContext);
        		String inputTypeStr = attrs.getAttributeValue(null,"inputType");
        		if(inputTypeStr != null) {
        			if(inputTypeStr.equals("textMultiLine")){
        				editText.setSingleLine(false);
        			}

        		}
            	String emsStr = attrs.getAttributeValue(null,"ems");
            	if(emsStr != null) {
            		editText.setEms(Integer.parseInt(emsStr));
            	}
        		retView = (View) editText;
        	}
        	else {
        		// ダイアログの表示
				AlertDialog.Builder dlg;
				dlg = new AlertDialog.Builder(mContext);
				dlg.setTitle("not implemented");
				dlg.setMessage("Create from Class " + name + " was not implemented in createView2.");
				dlg.show();
				retView = null;
        	}
        	String idStr = attrs.getAttributeValue(null,"id");
        	if(idStr != null && retView != null ) retView.setId(Integer.parseInt(idStr.substring(1)));
        	if (DEBUG) {
                System.out.println("new Instance of " + name + " was generated.");
            }
            return retView;
        }
        catch (IllegalArgumentException e) {
        	InflateException ie = new InflateException(attrs.getPositionDescription()
                    + ": Error inflating class(IllegalArgumentException in createView2) "
                    + (prefix != null ? (prefix + name) : name));
            ie.initCause(e);
            throw ie;
		} catch (java.lang.ClassCastException e) {
			e.printStackTrace();
			InflateException ie = new InflateException(attrs.getPositionDescription()
                    + ": Error inflating class(ClassCastException in createView2) "
                    + (prefix != null ? (prefix + name) : name));
            ie.initCause(e);
            throw ie;
		}
    }

    /**
     * This routine is responsible for creating the correct subclass of View
     * given the xml element name. Override it to handle custom view objects. If
     * you override this in your subclass be sure to call through to
     * super.onCreateView(name) for names you do not recognize.
     *
     * @param name The fully qualified class name of the View to be create.
     * @param attrs An AttributeSet of attributes to apply to the View.
     *
     * @return View The View created.
     */
    protected View onCreateView(String name, AttributeSet attrs)
            throws ClassNotFoundException {
        return createView2(name, "android.view.", attrs);
    }

    /*
     * default visibility so the BridgeInflater can override it.
     */
    View createViewFromTag(String name, AttributeSet attrs) {
        if (name.equals("view")) {
            name = attrs.getAttributeValue(null, "class");
        }

        if (DEBUG) System.out.println("******** Creating view: " + name);

        try {
            View view = null;

            if (view == null) {
                if (-1 == name.indexOf('.')) {
                    view = onCreateView(name, attrs);
                } else {
                    view = createView2(name, null, attrs);
                }
            }

            if (DEBUG) System.out.println("Created view is: " + name);
            return view;

        } catch (InflateException e) {
            throw e;

        } catch (ClassNotFoundException e) {
            InflateException ie = new InflateException(attrs.getPositionDescription()
                    + ": Error inflating class(ClassNotFoundException in createViewFromTag) " + name);
            ie.initCause(e);
            throw ie;

        }
//        catch (Exception e) {
//            InflateException ie = new InflateException(attrs.getPositionDescription()
//                    + ": Error inflating class(in createViewFromTag) " + name);
//            ie.initCause(e);
//            throw ie;
//        }
    }

    /**
     * Recursive method used to descend down the xml hierarchy and instantiate
     * views, instantiate their children, and then call onFinishInflate().
     */
    private void rInflate(XmlPullParser parser, View parent, final AttributeSet attrs)
            throws XmlPullParserException, IOException {

        final int depth = parser.getDepth();
        int type;

        while (((type = parser.next()) != XmlPullParser.END_TAG ||
                parser.getDepth() > depth) && type != XmlPullParser.END_DOCUMENT) {

            if (type != XmlPullParser.START_TAG) {
                continue;
            }

            final String name = parser.getName();

            if (TAG_REQUEST_FOCUS.equals(name)) {
                parseRequestFocus(parser, parent);
            } else if (TAG_INCLUDE.equals(name)) {
                if (parser.getDepth() == 0) {
                    throw new InflateException("<include /> cannot be the root element");
                }
                parseInclude(parser, parent, attrs);
            } else if (TAG_MERGE.equals(name)) {
                throw new InflateException("<merge /> must be the root element");
            } else {
            	if (DEBUG) System.out.println("rInflate: " + name);
                final View view = createViewFromTag(name, attrs);
                final ViewGroup viewGroup = (ViewGroup) parent;
                //final ViewGroup.LayoutParams params = viewGroup.generateLayoutParams(attrs);
                String widthStr = attrs.getAttributeValue(null,"layout_width");
                String heightStr = attrs.getAttributeValue(null,"layout_height");
                String alnParTopStr = attrs.getAttributeValue(null,"layout_alignParentTop");
                String alnParBtmStr = attrs.getAttributeValue(null,"layout_alignParentBottom");
                String alnParLeftStr = attrs.getAttributeValue(null,"layout_alignParentLeft");
                String aboveStr = attrs.getAttributeValue(null,"layout_above");
                String toRightOfStr = attrs.getAttributeValue(null,"layout_toRightOf");
                rInflate(parser, view, attrs);
                int width = 0;
                int height = 0;
                if(widthStr != null){
	                if(widthStr.matches(".+dip")) {
	                	width = (int)(Double.parseDouble(widthStr.replace("dip", "")) / mMetrics.scaledDensity);
	                }
	                else {
	                	width = Integer.parseInt(widthStr);
	                }
                }
                if(heightStr != null){
	                if(heightStr.matches(".+dip")) {
	                	height = (int)(Double.parseDouble(heightStr.replace("dip", "")) / mMetrics.scaledDensity);
	                }
	                else {
	                	height = Integer.parseInt(heightStr);
	                }
                }
                if(width == 0 && height == 0) {
                	if (DEBUG) System.out.println("addView :" + name);
                	viewGroup.addView(view);
                } else {
                	RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width,height);
                	if(alnParTopStr != null) {
                		lp.addRule(RelativeLayout.ALIGN_PARENT_TOP,Boolean.valueOf(alnParTopStr) ? 0 : -1);
                		if (DEBUG) System.out.println(name + "is align parent top ");
                	}
                	if(alnParBtmStr != null) {
                		lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,Boolean.valueOf(alnParTopStr) ? 0 : -1);
                		if (DEBUG) System.out.println(name + "is align parent bottom ");
                	}
                	if(alnParLeftStr != null) {
                		lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT,Boolean.valueOf(alnParTopStr) ? 0 : -1);
                		if (DEBUG) System.out.println(name + "is align parent left ");
                	}
                	if(aboveStr != null) {
                		int id = Integer.parseInt(aboveStr.substring(1));
                		lp.addRule(RelativeLayout.ABOVE,id);
                		if (DEBUG) System.out.println(name + "is above " + id);
                	}
                	if(toRightOfStr != null) {
                		int id = Integer.parseInt(toRightOfStr.substring(1));
                		lp.addRule(RelativeLayout.RIGHT_OF,id);
                		if (DEBUG) System.out.println(name + "is right of " + id);
                	}
                	viewGroup.addView(view, lp /*params*/);
                	if (DEBUG) System.out.println("addView :" + name + ",width = " + width + ",height = " + height);
                }
            }
        }

        //parent.onFinishInflate()
    }

    private void parseRequestFocus(XmlPullParser parser, View parent)
            throws XmlPullParserException, IOException {
        int type;
        parent.requestFocus();
        final int currentDepth = parser.getDepth();
        while (((type = parser.next()) != XmlPullParser.END_TAG ||
                parser.getDepth() > currentDepth) && type != XmlPullParser.END_DOCUMENT) {
            // Empty
        }
    }

    private void parseInclude(XmlPullParser parser, View parent, AttributeSet attrs)
            throws XmlPullParserException, IOException {

        int type;

        if (parent instanceof ViewGroup) {
            final int layout = attrs.getAttributeResourceValue(null, "layout", 0);
            if (layout == 0) {
                final String value = attrs.getAttributeValue(null, "layout");
                if (value == null) {
                    throw new InflateException("You must specifiy a layout in the"
                            + " include tag: <include layout=\"@layout/layoutID\" />");
                } else {
                    throw new InflateException("You must specifiy a valid layout "
                            + "reference. The layout ID " + value + " is not valid.");
                }
            } else {
                final XmlResourceParser childParser =
                        getContext().getResources().getLayout(layout);

                try {
                    final AttributeSet childAttrs = Xml.asAttributeSet(childParser);

                    while ((type = childParser.next()) != XmlPullParser.START_TAG &&
                            type != XmlPullParser.END_DOCUMENT) {
                        // Empty.
                    }

                    if (type != XmlPullParser.START_TAG) {
                        throw new InflateException(childParser.getPositionDescription() +
                                ": No start tag found!");
                    }

                    final String childName = childParser.getName();

                    if (TAG_MERGE.equals(childName)) {
                        // Inflate all children.
                        rInflate(childParser, parent, childAttrs);
                    } else {
                        final View view = createViewFromTag(childName, childAttrs);
                        final ViewGroup group = (ViewGroup) parent;

                        // We try to load the layout params set in the <include /> tag. If
                        // they don't exist, we will rely on the layout params set in the
                        // included XML file.
                        // During a layoutparams generation, a runtime exception is thrown
                        // if either layout_width or layout_height is missing. We catch
                        // this exception and set localParams accordingly: true means we
                        // successfully loaded layout params from the <include /> tag,
                        // false means we need to rely on the included layout params.
                        ViewGroup.LayoutParams params = null;
                        try {
                            params = group.generateLayoutParams(attrs);
                        } catch (RuntimeException e) {
                            params = group.generateLayoutParams(childAttrs);
                        } finally {
                            if (params != null) {
                                view.setLayoutParams(params);
                            }
                        }

                        // Inflate all children.
                        rInflate(childParser, view, childAttrs);

//                        // Attempt to override the included layout's android:id with the
//                        // one set on the <include /> tag itself.
//                        TypedArray a = mContext.obtainStyledAttributes(attrs,
//                        		com.android.internal.R.styleable.View, 0, 0);
//                        int id = a.getResourceId(com.android.internal.R.styleable.View_id, View.NO_ID);
//                        // While we're at it, let's try to override android:visibility.
//                        int visibility = a.getInt(com.android.internal.R.styleable.View_visibility, -1);
//                        a.recycle();
//
//                        if (id != View.NO_ID) {
//                            view.setId(id);
//                        }
//
//                        switch (visibility) {
//                            case 0:
//                                view.setVisibility(View.VISIBLE);
//                                break;
//                            case 1:
//                                view.setVisibility(View.INVISIBLE);
//                                break;
//                            case 2:
//                                view.setVisibility(View.GONE);
//                                break;
//                        }
                        view.setVisibility(View.VISIBLE);

                        group.addView(view);
                    }
                } finally {
                    childParser.close();
                }
            }
        } else {
            throw new InflateException("<include /> can only be used inside of a ViewGroup");
        }

        final int currentDepth = parser.getDepth();
        while (((type = parser.next()) != XmlPullParser.END_TAG ||
                parser.getDepth() > currentDepth) && type != XmlPullParser.END_DOCUMENT) {
            // Empty
        }
    }
}